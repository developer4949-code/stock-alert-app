package com.stocksentry.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stocksentry.app.ui.screens.WelcomeScreen
import com.stocksentry.app.ui.screens.PasswordLockScreen
import com.stocksentry.app.ui.screens.MainWatchlistScreen
import com.stocksentry.app.ui.screens.WatchlistDetailScreen
import com.stocksentry.app.ui.screens.ShareWatchlistScreen
import com.stocksentry.app.ui.screens.NewsScreen
import com.stocksentry.app.ui.screens.NotificationPreferencesScreen
import android.util.Log

object Routes {
    const val WELCOME = "welcome"
    const val PASSWORD_LOCK = "password_lock"
    const val MAIN_WATCHLIST = "main_watchlist"
    const val WATCHLIST_DETAIL = "watchlist_detail/{watchlistId}"
    const val SHARE_WATCHLIST = "share_watchlist/{watchlistId}"
    const val NEWS = "news/{symbol}"
    const val SHARED_WATCHLIST = "shared_watchlist/{otp}"
    const val NOTIFICATION_PREFERENCES = "notification_preferences"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // Debug logging for routes
    Log.d("NavGraph", "Creating NavHost with routes:")
    Log.d("NavGraph", "SHARE_WATCHLIST: ${Routes.SHARE_WATCHLIST}")
    Log.d("NavGraph", "WATCHLIST_DETAIL: ${Routes.WATCHLIST_DETAIL}")
    Log.d("NavGraph", "NEWS: ${Routes.NEWS}")
    
    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) { WelcomeScreen(navController) }
        composable(Routes.PASSWORD_LOCK) { PasswordLockScreen(navController) }
        composable(Routes.MAIN_WATCHLIST) { MainWatchlistScreen(navController) }
        composable(Routes.WATCHLIST_DETAIL) { backStackEntry ->
            val watchlistId = backStackEntry.arguments?.getString("watchlistId") ?: ""
            WatchlistDetailScreen(navController, watchlistId)
        }
        composable(Routes.SHARE_WATCHLIST) { backStackEntry ->
            val watchlistId = backStackEntry.arguments?.getString("watchlistId") ?: ""
            Log.d("NavGraph", "SHARE_WATCHLIST route - watchlistId: $watchlistId")
            Log.d("NavGraph", "All arguments: ${backStackEntry.arguments}")
            Log.d("NavGraph", "Route pattern: ${Routes.SHARE_WATCHLIST}")
            Log.d("NavGraph", "Parameter names: ${backStackEntry.arguments?.keySet()}")
            ShareWatchlistScreen(navController, watchlistId)
        }
        composable(Routes.NEWS) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            NewsScreen(navController, symbol)
        }
        composable(Routes.SHARED_WATCHLIST) { backStackEntry ->
            val otp = backStackEntry.arguments?.getString("otp") ?: ""
            // Handle deep link: Fetch shared watchlist with OTP
            WatchlistDetailScreen(navController, "", otp) // Pass OTP for shared view
        }
        composable(Routes.NOTIFICATION_PREFERENCES) { 
            NotificationPreferencesScreen(navController) 
        }
    }
}

