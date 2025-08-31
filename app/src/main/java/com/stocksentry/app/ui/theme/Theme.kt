package com.stocksentry.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = DarkPink,
    secondary = LightGreen,
    background = Color.White
)

@Composable
fun StockSentryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}


// Gradient Brush for backgrounds
val GradientBrush = Brush.linearGradient(
    colors = listOf(DarkPink, LightGreen)
)

