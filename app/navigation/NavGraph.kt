@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // Debug logging for routes
    Log.d("NavGraph", "Creating NavHost with routes:")
    Log.d("NavGraph", "SHARE_WATCHLIST: ${Routes.SHARE_WATCHLIST}")
    Log.d("NavGraph", "WATCHLIST_DETAIL: ${Routes.WATCHLIST_DETAIL}")
    Log.d("NavGraph", "NEWS: ${Routes.NEWS}")
    
    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.SHARE_WATCHLIST) { backStackEntry ->
            val watchlistId = backStackEntry.arguments?.getString("watchlistId") ?: ""
            Log.d("NavGraph", "SHARE_WATCHLIST route - watchlistId: $watchlistId")
            Log.d("NavGraph", "All arguments: ${backStackEntry.arguments}")
            Log.d("NavGraph", "Route pattern: ${Routes.SHARE_WATCHLIST}")
            Log.d("NavGraph", "Parameter names: ${backStackEntry.arguments?.keySet()}")
            ShareWatchlistScreen(navController, watchlistId)
        }
