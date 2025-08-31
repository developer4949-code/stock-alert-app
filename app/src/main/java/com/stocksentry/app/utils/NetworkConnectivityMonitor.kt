package com.stocksentry.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.stocksentry.app.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var isConnected = false

    init {
        setupNetworkCallback()
        checkInitialConnectivity()
    }

    private fun setupNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isConnected) {
                    Log.d("NetworkMonitor", "Network became available, triggering sync...")
                    isConnected = true
                    triggerSync()
                }
            }

            override fun onLost(network: Network) {
                Log.d("NetworkMonitor", "Network became unavailable")
                isConnected = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val wasConnected = isConnected
                isConnected = hasInternet

                if (!wasConnected && hasInternet) {
                    Log.d("NetworkMonitor", "Internet capability restored, triggering sync...")
                    triggerSync()
                }
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun checkInitialConnectivity() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        
        if (isConnected) {
            Log.d("NetworkMonitor", "Initial network check: Connected, triggering sync...")
            triggerSync()
        } else {
            Log.d("NetworkMonitor", "Initial network check: Not connected")
        }
    }

    private fun triggerSync() {
        try {
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
            workManager.enqueue(syncRequest)
            Log.d("NetworkMonitor", "Sync work enqueued successfully")
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Failed to enqueue sync work", e)
        }
    }

    fun isNetworkAvailable(): Boolean = isConnected

    fun triggerManualSync() {
        Log.d("NetworkMonitor", "Manual sync triggered")
        triggerSync()
    }
}
