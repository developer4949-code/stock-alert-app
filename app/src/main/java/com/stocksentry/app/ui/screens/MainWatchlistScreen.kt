package com.stocksentry.app.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.stocksentry.app.data.local.WatchlistEntity
import com.stocksentry.app.navigation.Routes
import com.stocksentry.app.ui.theme.GradientBrush
import com.stocksentry.app.viewmodels.WatchlistViewModel
import com.stocksentry.app.data.models.Watchlist

@Composable
fun MainWatchlistScreen(navController: NavController) {
    val viewModel: WatchlistViewModel = hiltViewModel()
    var watchlists: List<WatchlistEntity> by remember { mutableStateOf(listOf()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Load watchlists on first launch
    LaunchedEffect(Unit) {
        viewModel.loadWatchlists { watchlistList ->
            watchlists = watchlistList
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Watchlists",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Manual sync button
                    IconButton(
                        onClick = { 
                            viewModel.triggerManualSync()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    IconButton(
                        onClick = { navController.navigate(Routes.NOTIFICATION_PREFERENCES) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Watchlist")
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                // Watchlist items
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (watchlists.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No Watchlists Yet",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Create your first watchlist to start tracking stocks",
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(watchlists) { watchlist ->
                            WatchlistCard(
                                watchlist = watchlist,
                                onWatchlistClick = { 
                                    navController.navigate(Routes.WATCHLIST_DETAIL.replace("{watchlistId}", watchlist.id))
                                },
                                onShareClick = {
                                    val route = Routes.SHARE_WATCHLIST.replace("{watchlistId}", watchlist.id)
                                    Log.d("MainWatchlistScreen", "Original route: ${Routes.SHARE_WATCHLIST}")
                                    Log.d("MainWatchlistScreen", "Watchlist ID: ${watchlist.id}")
                                    Log.d("MainWatchlistScreen", "Final route: $route")
                                    Log.d("MainWatchlistScreen", "Route length: ${route.length}")
                                    navController.navigate(route)
                                },
                                onDeleteClick = {
                                    viewModel.deleteWatchlist(watchlist.id) {
                                        // Refresh the list after delete completes
                                        viewModel.loadWatchlists { watchlistList ->
                                            watchlists = watchlistList
                                        }
                                    }
                                }

                            )
                        }
                    }
                }
            }
        }

        // Create Watchlist Dialog
        if (showCreateDialog) {
            CreateWatchlistDialog(
                onDismiss = { showCreateDialog = false },
                onWatchlistCreated = { name, symbols ->
                    viewModel.createWatchlist(name, symbols) {
                        // Refresh the list
                        viewModel.loadWatchlists { watchlistList ->
                            watchlists = watchlistList
                        }
                        showCreateDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun WatchlistCard(
    watchlist: WatchlistEntity,
    onWatchlistClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onWatchlistClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = watchlist.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${watchlist.symbols.size} stocks",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                if (watchlist.symbols.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = watchlist.symbols.take(3).joinToString(", "),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onShareClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CreateWatchlistDialog(
    onDismiss: () -> Unit,
    onWatchlistCreated: (String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var symbols by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Watchlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Watchlist Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = symbols,
                    onValueChange = { symbols = it },
                    label = { Text("Stock Symbols (comma separated)") },
                    placeholder = { Text("AAPL, GOOGL, MSFT") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val symbolList = symbols.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onWatchlistCreated(name, symbolList)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


