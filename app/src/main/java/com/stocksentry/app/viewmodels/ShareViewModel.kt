package com.stocksentry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stocksentry.app.data.models.ShareRequest
import com.stocksentry.app.data.models.Watchlist
import com.stocksentry.app.data.remote.StockSentryApi
import com.stocksentry.app.data.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val api: StockSentryApi,
    private val userDao: UserDao
) : ViewModel() {

    fun shareWatchlist(watchlistId: String, email: String, onSuccess: (String) -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val deepLink = api.shareWatchlist(ShareRequest(watchlistId, email))
                onSuccess(deepLink)
            } catch (e: Exception) {
                // Handle error gracefully
                e.printStackTrace()
                val errorMessage = when {
                    e.message?.contains("502") == true -> "Server temporarily unavailable. Please try again later."
                    e.message?.contains("500") == true -> "Server error occurred. Please try again later."
                    e.message?.contains("404") == true -> "Service not found. Please check your connection."
                    e.message?.contains("timeout") == true -> "Request timed out. Please check your internet connection."
                    else -> "Failed to share watchlist. Please try again."
                }
                onError(errorMessage)
                // Return a fallback deep link as backup
                onSuccess("stocksentry://share/$watchlistId")
            }
        }
    }

    fun getWatchlistById(watchlistId: String, onComplete: (Watchlist?) -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = userDao.getUser()
                if (user != null) {
                    val watchlists = api.getWatchlists(user.userId)
                    val watchlist = watchlists.find { it.id == watchlistId }
                    onComplete(watchlist)
                } else {
                    onError("User not found. Please login again.")
                    onComplete(null)
                }
            } catch (e: Exception) {
                // Handle error gracefully
                e.printStackTrace()
                val errorMessage = when {
                    e.message?.contains("502") == true -> "Server temporarily unavailable. Please try again later."
                    e.message?.contains("500") == true -> "Server error occurred. Please try again later."
                    e.message?.contains("404") == true -> "Service not found. Please check your connection."
                    e.message?.contains("timeout") == true -> "Request timed out. Please check your internet connection."
                    else -> "Failed to load watchlist. Please try again."
                }
                onError(errorMessage)
                onComplete(null)
            }
        }
    }
}


