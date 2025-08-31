package com.stocksentry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stocksentry.app.data.remote.StockSentryApi
import com.stocksentry.app.data.models.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val api: StockSentryApi
) : ViewModel() {

    fun getNewsForSymbol(symbol: String, onComplete: (NewsResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.getNews(symbol)
                onComplete(response)
            } catch (e: Exception) {
                // Handle error - return null for now
                onComplete(null)
            }
        }
    }

    fun checkForAlerts(symbol: String, onAlertDetected: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.getNews(symbol)
                val hasAlerts = response.articles.any { article ->
                    val text = (article.title + " " + article.content).lowercase()
                    text.contains("earnings") || text.contains("acquisition") || text.contains("merger")
                }
                
                if (hasAlerts) {
                    onAlertDetected("Alert detected for $symbol")
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
