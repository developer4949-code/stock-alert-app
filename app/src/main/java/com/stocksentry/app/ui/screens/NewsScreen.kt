package com.stocksentry.app.ui.screens


import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.stocksentry.app.ui.theme.GradientBrush
import com.stocksentry.app.ui.theme.DarkPink
import com.stocksentry.app.viewmodels.NewsViewModel
import com.stocksentry.app.data.models.NewsResponse
import com.stocksentry.app.data.models.Article
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsScreen(navController: NavController, symbol: String) {
    val viewModel: NewsViewModel = hiltViewModel()
    var newsResponse by remember { mutableStateOf<NewsResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasAlerts by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val articles = remember(newsResponse) {
        newsResponse?.articles?.filterNotNull() ?: emptyList()
    }

    // Load news on first launch
    LaunchedEffect(symbol) {
        try {
            viewModel.getNewsForSymbol(symbol) { response ->
                newsResponse = response
                isLoading = false
                hasError = false
                hasAlerts = response?.articles?.any { article ->
                    article.title.contains("earnings", ignoreCase = true) ||
                            article.title.contains("acquisition", ignoreCase = true) ||
                            article.title.contains("merger", ignoreCase = true)
                } ?: false
            }
        } catch (e: Exception) {
            isLoading = false
            hasError = true
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "News for $symbol",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (hasAlerts) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = DarkPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Alerts detected",
                                fontSize = 12.sp,
                                color = DarkPink,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                viewModel.getNewsForSymbol(symbol) { response ->
                                    newsResponse = response
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                hasError = true
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
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
            } else if (hasError) {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Failed to load news",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please check your connection and try again",
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isLoading = true
                                hasError = false
                                coroutineScope.launch {
                                    try {
                                        viewModel.getNewsForSymbol(symbol) { response ->
                                            newsResponse = response
                                            isLoading = false
                                            hasError = false
                                            hasAlerts = response?.articles?.any { article ->
                                                article.title.contains("earnings", ignoreCase = true) ||
                                                        article.title.contains("acquisition", ignoreCase = true) ||
                                                        article.title.contains("merger", ignoreCase = true)
                                            } ?: false
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        hasError = true
                                    }
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                // News articles
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (articles.isEmpty()) {
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
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No News Available",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Check back later for updates on $symbol",
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = articles,
                            key = { article -> article.url.ifEmpty { article.title } }
                        ) { article ->
                            NewsArticleCard(article = article, symbol = symbol)
                        }
                    }
                }
            }
        }
    }
}

// ---------- Helper functions and NewsArticleCard stay the same as in your code ----------


// Helper function to format date safely (outside of Composable)
private fun formatDateSafely(publishedAt: String): String {
    if (publishedAt.isEmpty()) return "Date unavailable"
    
    // Try multiple date formats that are commonly used by news APIs
    val dateFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )
    
    var parsedDate: Date? = null
    for (format in dateFormats) {
        try {
            val inputFormat = SimpleDateFormat(format, Locale.getDefault())
            inputFormat.isLenient = false
            parsedDate = inputFormat.parse(publishedAt)
            if (parsedDate != null) break
        } catch (e: Exception) {
            // Continue to next format
        }
    }
    
    return if (parsedDate != null) {
        try {
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            "Date unavailable"
        }
    } else {
        "Date unavailable"
    }
}

// Helper function to share news article safely (outside of Composable)
private fun shareNewsArticle(context: android.content.Context, article: Article, symbol: String, formattedDate: String) {
    try {
        val shareText = buildString {
            append("📰 News Alert for $symbol\n\n")
            append("${article.title}\n\n")
            if (article.description.isNotEmpty()) {
                append("${article.description}\n\n")
            }
            append("Source: ${article.sourceUrl.ifEmpty { "Unknown Source" }}\n")
            append("Published: $formattedDate\n\n")
            if (article.url.isNotEmpty()) {
                append("Read more: ${article.url}")
            }
        }
        
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        val shareIntent = Intent.createChooser(sendIntent, "Share News")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        // Handle any errors during sharing
        // You could show a toast message here if needed
    }
}

// Helper function to open news URL safely (outside of Composable)
private fun openNewsUrl(context: android.content.Context, url: String) {
    if (url.isEmpty()) return
    
    try {
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
        // Check if there's an app to handle this intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // No app available to handle the URL
            // You could show a toast message here
        }
    } catch (e: Exception) {
        // Handle error if URL is malformed or other issues
        // You could show a toast message here
    }
}

@Composable
fun NewsArticleCard(
    article: Article,
    symbol: String
) {
    val context = LocalContext.current
    val isAlert = article.title.contains("earnings", ignoreCase = true) ||
            article.title.contains("acquisition", ignoreCase = true) ||
            article.title.contains("merger", ignoreCase = true)

    // Format the published date safely
    val formattedDate = remember(article.publishedAt) {
        formatDateSafely(article.publishedAt)
    }

    // Safely truncate content
    val safeContent = remember(article.content) {
        try {
            if (article.content.length > 500) {
                article.content.take(500) + "..."
            } else {
                article.content
            }
        } catch (e: Exception) {
            "Content unavailable"
        }
    }

    // Safely get title
    val safeTitle = remember(article.title) {
        try {
            article.title.ifEmpty { "Title unavailable" }
        } catch (e: Exception) {
            "Title unavailable"
        }
    }

    // Safely get description
    val safeDescription = remember(article.description) {
        try {
            article.description.ifEmpty { "" }
        } catch (e: Exception) {
            ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with alert indicator and share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (isAlert) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = DarkPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ALERT",
                                fontSize = 10.sp,
                                color = DarkPink,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = safeTitle,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = {
                        shareNewsArticle(context, article, symbol, formattedDate)
                    },
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
            }

            // Date (always shown)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Published",
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formattedDate,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                // ✅ Show Source ONLY if it exists
                if (article.sourceUrl.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Source,
                            contentDescription = "Source",
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = article.sourceUrl,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Description
            if (safeDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = safeDescription,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            // Content (truncated)
            if (safeContent.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = safeContent,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Read more button
            if (article.url.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(article.url)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // ✅ important
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = "Open",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read Full Article")
                }
            }
        }
    }
}


