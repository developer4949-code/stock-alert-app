package com.stocksentry.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stocksentry.app.data.local.WatchlistDao
import com.stocksentry.app.data.local.UserDao
import com.stocksentry.app.data.remote.StockSentryApi
import com.stocksentry.app.data.models.Watchlist
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.hilt.work.HiltWorker
import android.util.Log

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val api: StockSentryApi,
    private val watchlistDao: WatchlistDao,
    private val userDao: UserDao
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val currentUser = userDao.getUser()
            if (currentUser == null) {
                Log.e("SyncWorker", "No user logged in, skipping sync")
                return Result.success()
            }

            val pending = watchlistDao.getPendingSyncWatchlists()
            Log.d("SyncWorker", "Found ${pending.size} pending watchlists to sync")

            if (pending.isEmpty()) {
                Log.d("SyncWorker", "No pending watchlists to sync")
                return Result.success()
            }

            var successCount = 0
            var failureCount = 0

            for (watchlist in pending) {
                if (watchlist.userId == currentUser.userId) {
                    try {
                        if (watchlist.isDeleted) {
                            Log.d("SyncWorker", "Deleting watchlist: ${watchlist.id}")
                            api.deleteWatchlist(watchlist.id)
                            watchlistDao.deleteWatchlist(watchlist.id)
                            successCount++
                            Log.d("SyncWorker", "Successfully deleted watchlist: ${watchlist.id}")
                        } else {
                            Log.d("SyncWorker", "Upserting watchlist: ${watchlist.id}")
                            val dto = Watchlist(
                                id = watchlist.id,
                                userId = watchlist.userId,
                                name = watchlist.name,
                                symbols = watchlist.symbols
                            )
                            api.upsertWatchlist(dto)
                            watchlistDao.markAsSynced(watchlist.id)
                            successCount++
                            Log.d("SyncWorker", "Successfully synced watchlist: ${watchlist.id}")
                        }
                    } catch (e: Exception) {
                        failureCount++
                        Log.e("SyncWorker", "Error syncing watchlist: ${watchlist.id}", e)
                        if (isNetworkError(e)) {
                            Log.w("SyncWorker", "Network error detected, will retry later")
                        } else {
                            Log.e("SyncWorker", "Non-network error, marking as failed")
                        }
                    }
                } else {
                    Log.d("SyncWorker", "Skipping watchlist ${watchlist.id} - belongs to different user")
                }
            }

            Log.d("SyncWorker", "Sync completed. Success: $successCount, Failures: $failureCount")
            if (failureCount > 0) {
                Log.d("SyncWorker", "Some items failed, requesting retry")
                return Result.retry()
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in sync work", e)
            return if (isNetworkError(e)) Result.retry() else Result.failure()
        }
    }

    private fun isNetworkError(exception: Exception): Boolean {
        return when (exception) {
            is java.net.UnknownHostException,
            is java.net.ConnectException,
            is java.net.SocketTimeoutException,
            is java.net.SocketException,
            is java.io.IOException -> true
            else -> false
        }
    }
}
