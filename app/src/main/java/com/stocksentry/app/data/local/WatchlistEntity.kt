package com.stocksentry.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val stocks: List<String>, // or JSON string if needed
    val isSynced: Boolean = false, // ✅ New: false = pending sync
    val isDeleted: Boolean = false, // ✅ New: mark deletions until synced
    val watchlistId: String,
    val symbol: String,
    val symbols: List<String>
)
