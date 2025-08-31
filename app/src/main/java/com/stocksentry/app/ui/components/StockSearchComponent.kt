package com.stocksentry.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StockSearchComponent(
    onStockSelected: (String) -> Unit,
    selectedStocks: List<String> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    
    // Popular stocks for quick selection
    val popularStocks = listOf(
        "AAPL" to "Apple Inc.",
        "GOOGL" to "Alphabet Inc.",
        "MSFT" to "Microsoft Corporation",
        "AMZN" to "Amazon.com Inc.",
        "TSLA" to "Tesla Inc.",
        "META" to "Meta Platforms Inc.",
        "NVDA" to "NVIDIA Corporation",
        "NFLX" to "Netflix Inc.",
        "JPM" to "JPMorgan Chase & Co.",
        "JNJ" to "Johnson & Johnson"
    )
    
    // Filter stocks based on search query
    val filteredStocks = if (searchQuery.isBlank()) {
        popularStocks
    } else {
        popularStocks.filter { (symbol, name) ->
            symbol.contains(searchQuery.uppercase()) || 
            name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                showResults = it.isNotBlank()
            },
            label = { Text("Search stocks...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected stocks
        if (selectedStocks.isNotEmpty()) {
            Text(
                text = "Selected Stocks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
                         LazyRow(
                 horizontalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 items(selectedStocks) { symbol ->
                     AssistChip(
                         onClick = { /* Remove stock */ },
                         label = { Text(symbol) },
                         colors = AssistChipDefaults.assistChipColors(
                             containerColor = MaterialTheme.colorScheme.primary
                         )
                     )
                 }
             }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search results or popular stocks
        if (showResults && filteredStocks.isNotEmpty()) {
            Text(
                text = "Search Results",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredStocks) { (symbol, name) ->
                    StockSearchItem(
                        symbol = symbol,
                        name = name,
                        isSelected = selectedStocks.contains(symbol),
                        onClick = { onStockSelected(symbol) }
                    )
                }
            }
        } else if (!showResults) {
            Text(
                text = "Popular Stocks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(popularStocks) { (symbol, name) ->
                    StockSearchItem(
                        symbol = symbol,
                        name = name,
                        isSelected = selectedStocks.contains(symbol),
                        onClick = { onStockSelected(symbol) }
                    )
                }
            }
        }
    }
}

@Composable
fun StockSearchItem(
    symbol: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
