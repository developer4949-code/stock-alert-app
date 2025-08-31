package com.stocksentry.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.stocksentry.app.navigation.Routes
import com.stocksentry.app.ui.theme.GradientBrush
import com.stocksentry.app.viewmodels.WatchlistViewModel
import com.stocksentry.app.data.models.Watchlist
import com.stocksentry.app.ui.components.StockSearchComponent
import kotlinx.coroutines.launch

@Composable
fun WatchlistDetailScreen(
    navController: NavController,
    watchlistId: String,
    otp: String = ""
) {
    val viewModel: WatchlistViewModel = hiltViewModel()
    var watchlist by remember { mutableStateOf<Watchlist?>(null) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // ✅ Load watchlist details from DB, not hardcoded
    LaunchedEffect(watchlistId) {
        if (watchlistId.isNotEmpty()) {
            viewModel.getWatchlistById(watchlistId) { watchlistData ->
                watchlist = watchlistData as Watchlist?
                isLoading = false
            }
        } else {
            // Handle shared watchlist with OTP
            // TODO: load from server using OTP if required
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
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = watchlist?.name ?: "Watchlist Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (watchlist != null) {
                        Text(
                            text = "${watchlist!!.symbols.size} stocks",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showAddStockDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Stock",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(
                        onClick = {
                            val route = Routes.SHARE_WATCHLIST.replace("{watchlistId}", watchlistId)
                            navController.navigate(route)
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
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
                if (watchlist?.symbols?.isEmpty() != false) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Stocks Added",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add stocks to start tracking",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddStockDialog = true }
                            ) {
                                Text("Add Stocks")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(watchlist?.symbols ?: emptyList()) { symbol ->
                            StockCard(
                                symbol = symbol,
                                onNewsClick = {
                                    navController.navigate(
                                        Routes.NEWS.replace("{symbol}", symbol)
                                    )
                                },
                                onRemoveClick = {
                                    scope.launch {
                                        viewModel.removeStockFromWatchlist(watchlistId, symbol)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Add Stock Dialog
        if (showAddStockDialog) {
            AddStockDialog(
                onDismiss = { showAddStockDialog = false },
                onStocksAdded = { newSymbols ->
                    scope.launch {
                        viewModel.addStocksToWatchlist(watchlistId, newSymbols)
                        watchlist = watchlist?.copy(
                            symbols = watchlist!!.symbols + newSymbols
                        )
                    }
                    showAddStockDialog = false
                }
            )
        }
    }
}

@Composable
fun StockCard(
    symbol: String,
    onNewsClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNewsClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = symbol,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tap to view news",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNewsClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Article,
                        contentDescription = "View News",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onRemoveClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Stock",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddStockDialog(
    onDismiss: () -> Unit,
    onStocksAdded: (List<String>) -> Unit
) {
    var selectedStocks by remember { mutableStateOf(emptyList<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Stocks") },
        text = {
            StockSearchComponent(
                onStockSelected = { symbol ->
                    if (selectedStocks.contains(symbol)) {
                        selectedStocks = selectedStocks - symbol
                    } else {
                        selectedStocks = selectedStocks + symbol
                    }
                },
                selectedStocks = selectedStocks
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedStocks.isNotEmpty()) {
                        onStocksAdded(selectedStocks)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
