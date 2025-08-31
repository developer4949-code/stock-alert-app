package com.stocksentry.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import com.stocksentry.app.utils.NetworkConnectivityMonitor
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var networkConnectivityMonitor: NetworkConnectivityMonitor

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
