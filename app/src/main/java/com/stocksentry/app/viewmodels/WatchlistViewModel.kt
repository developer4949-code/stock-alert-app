package com.stocksentry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stocksentry.app.data.local.WatchlistEntity
import com.stocksentry.app.data.local.UserDao
import com.stocksentry.app.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: WatchlistRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _watchlists = MutableStateFlow<List<WatchlistEntity>>(emptyList())
    val watchlists: StateFlow<List<WatchlistEntity>> = _watchlists

    init {
        loadWatchlists()
    }
    fun createWatchlist(name: String, symbols: List<String>, onComplete: (List<WatchlistEntity>) -> Unit = {}) {
        viewModelScope.launch {
            // Get current user
            val currentUser = userDao.getUser()
            if (currentUser == null) {
                Log.e("WatchlistViewModel", "No user logged in, cannot create watchlist")
                onComplete(emptyList())
                return@launch
            }

            val generatedId = UUID.randomUUID().toString()

            val newWatchlist = WatchlistEntity(
                id = generatedId,
                userId = currentUser.userId,
                name = name,
                stocks = symbols,
                isSynced = false,
                isDeleted = false,
                watchlistId = generatedId,
                symbol = symbols.firstOrNull() ?: "",
                symbols = symbols
            )

            repository.addWatchlist(newWatchlist)

            // Return updated list
            val updatedList = repository.getAllWatchlists()
            onComplete(updatedList)
        }
    }


    fun addWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch {
            repository.addWatchlist(watchlist)
            loadWatchlists()
        }
    }

    fun deleteWatchlist(watchlistId: String) {
        viewModelScope.launch {
            repository.deleteWatchlist(watchlistId)
            loadWatchlists()
        }
    }

    fun addStocksToWatchlist(watchlistId: String, symbols: List<String>) {
        viewModelScope.launch {
            repository.addStocksToWatchlist(watchlistId, symbols)
        }
    }

    fun deleteWatchlist(watchlistId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteWatchlist(watchlistId)
            onComplete()
        }
    }
    fun loadWatchlists(onComplete: (List<WatchlistEntity>) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val watchlists = repository.getAllWatchlists()
                _watchlists.value = watchlists
                onComplete(watchlists)
            } catch (e: Exception) {
                Log.e("WatchlistViewModel", "Error loading watchlists", e)
                onComplete(emptyList())
            }
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            try {
                Log.d("WatchlistViewModel", "Manual sync triggered")
                repository.triggerManualSync()
            } catch (e: Exception) {
                Log.e("WatchlistViewModel", "Error triggering manual sync", e)
            }
        }
    }

    fun getWatchlistById(watchlistId: String, onResult: (WatchlistEntity?) -> Unit) {
        viewModelScope.launch {
            val watchlist = repository.getWatchlistById(watchlistId)
            onResult(watchlist)
        }
    }

    fun removeStockFromWatchlist(watchlistId: String, symbol: String) {
        viewModelScope.launch {
            repository.removeStockFromWatchlist(watchlistId, symbol)
        }
    }


}
