package com.stocksentry.app.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.ExistingWorkPolicy
import com.stocksentry.app.data.local.WatchlistDao
import com.stocksentry.app.data.local.UserDao
import com.stocksentry.app.data.local.WatchlistEntity
import com.stocksentry.app.data.models.Watchlist
import com.stocksentry.app.data.remote.StockSentryApi
import com.stocksentry.app.utils.NetworkConnectivityMonitor
import com.stocksentry.app.workers.SyncWorker
import android.util.Log
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val watchlistDao: WatchlistDao,
    private val userDao: UserDao,
    private val workManager: WorkManager,
    private val api: StockSentryApi,
    private val networkMonitor: NetworkConnectivityMonitor
) {

    private fun toDto(entity: WatchlistEntity): Watchlist = Watchlist(
        id = entity.id,
        userId = entity.userId,
        name = entity.name,
        symbols = entity.symbols
    )

    suspend fun addWatchlist(watchlist: WatchlistEntity) {
        try {
            api.upsertWatchlist(toDto(watchlist))
            watchlistDao.insertWatchlist(watchlist.copy(isSynced = true))
            Log.d("WatchlistRepository", "Watchlist pushed online and saved as synced")
        } catch (e: Exception) {
            Log.w("WatchlistRepository", "Online upsert failed, falling back to offline queue", e)
            watchlistDao.insertWatchlist(watchlist.copy(isSynced = false))
            enqueueSync()
        }
    }

    suspend fun deleteWatchlist(watchlistId: String) {
        try {
            api.deleteWatchlist(watchlistId)
            watchlistDao.deleteWatchlist(watchlistId)
            Log.d("WatchlistRepository", "Watchlist deleted on backend and locally")
        } catch (e: Exception) {
            Log.w("WatchlistRepository", "Online delete failed, marking for sync", e)
            watchlistDao.markAsDeleted(watchlistId)
            enqueueSync()
        }
    }

    suspend fun getAllWatchlists(): List<WatchlistEntity> {
        val currentUser = userDao.getUser()
        if (currentUser == null) {
            Log.e("WatchlistRepository", "No user logged in, returning empty watchlist")
            return emptyList()
        }
        return watchlistDao.getWatchlistsByUser(currentUser.userId)
    }

    private fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork(
            "watchlist_sync",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d("WatchlistRepository", "Sync work enqueued with constraints and backoff")
    }

    suspend fun getWatchlistById(watchlistId: String): WatchlistEntity? {
        return watchlistDao.getWatchlistById(watchlistId)
    }

    suspend fun addStocksToWatchlist(watchlistId: String, symbols: List<String>) {
        val existingWatchlist = watchlistDao.getWatchlistById(watchlistId)
        existingWatchlist?.let { watchlist ->
            val updatedSymbols = watchlist.symbols + symbols
            val updatedWatchlist = watchlist.copy(
                symbols = updatedSymbols
            )
            try {
                api.upsertWatchlist(toDto(updatedWatchlist))
                watchlistDao.insertWatchlist(updatedWatchlist.copy(isSynced = true))
                Log.d("WatchlistRepository", "Stocks added and synced online")
            } catch (e: Exception) {
                Log.w("WatchlistRepository", "Online upsert failed, queueing for sync", e)
                watchlistDao.insertWatchlist(updatedWatchlist.copy(isSynced = false))
                enqueueSync()
            }
        }
    }

    suspend fun removeStockFromWatchlist(watchlistId: String, symbol: String) {
        val existingWatchlist = watchlistDao.getWatchlistById(watchlistId)
        existingWatchlist?.let { watchlist ->
            val updatedSymbols = watchlist.symbols.filter { it != symbol }
            val updatedWatchlist = watchlist.copy(
                symbols = updatedSymbols
            )
            try {
                api.upsertWatchlist(toDto(updatedWatchlist))
                watchlistDao.insertWatchlist(updatedWatchlist.copy(isSynced = true))
                Log.d("WatchlistRepository", "Stock removed and synced online")
            } catch (e: Exception) {
                Log.w("WatchlistRepository", "Online upsert failed, queueing for sync", e)
                watchlistDao.insertWatchlist(updatedWatchlist.copy(isSynced = false))
                enqueueSync()
            }
        }
    }

    suspend fun upsertWatchlist(watchlist: WatchlistEntity) {
        try {
            api.upsertWatchlist(toDto(watchlist))
            watchlistDao.insertWatchlist(watchlist.copy(isSynced = true))
            Log.d("WatchlistRepository", "Watchlist upserted online and saved as synced")
        } catch (e: Exception) {
            Log.w("WatchlistRepository", "Online upsert failed, falling back to offline queue", e)
            watchlistDao.insertWatchlist(watchlist.copy(isSynced = false))
            enqueueSync()
        }
    }

    fun triggerManualSync() {
        Log.d("WatchlistRepository", "Manual sync triggered")
        enqueueSync()
    }
}
