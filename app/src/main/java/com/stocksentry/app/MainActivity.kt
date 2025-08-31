package com.stocksentry.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.stocksentry.app.navigation.AppNavHost
import com.stocksentry.app.ui.theme.StockSentryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockSentryTheme {
                val navController = rememberNavController()
                
                // Handle deep link
                val data = intent.data
                if (data != null && data.scheme == "stocksentry" && data.host == "share") {
                    val otp = data.lastPathSegment
                    // Navigate to shared watchlist screen with OTP
                    navController.navigate("shared_watchlist/$otp")
                }
                
                AppNavHost(navController = navController)
            }
        }
    }
}


