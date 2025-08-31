package com.stocksentry.app.data.models

data class Watchlist(
    val id: String,
    val userId: String,
    val name: String,
    val symbols: List<String>
)

