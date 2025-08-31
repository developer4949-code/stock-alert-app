package com.stocksentry.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Delete

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity)

    @Query("SELECT * FROM watchlists WHERE userId = :userId AND isDeleted = 0")
    suspend fun getWatchlistsByUser(userId: String): List<WatchlistEntity>

    @Query("SELECT * FROM watchlists WHERE isDeleted = 0")
    suspend fun getAllWatchlists(): List<WatchlistEntity>

    @Query("SELECT * FROM watchlists WHERE id = :watchlistId AND isDeleted = 0")
    suspend fun getWatchlistById(watchlistId: String): WatchlistEntity?

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Query("DELETE FROM watchlists WHERE id = :watchlistId")
    suspend fun deleteWatchlist(watchlistId: String)

    // ✅ New: get unsynced items
    @Query("SELECT * FROM watchlists WHERE isSynced = 0")
    suspend fun getPendingSyncWatchlists(): List<WatchlistEntity>

    // ✅ New: mark synced
    @Query("UPDATE watchlists SET isSynced = 1 WHERE id = :watchlistId")
    suspend fun markAsSynced(watchlistId: String)

    // ✅ New: mark as deleted (soft delete)
    @Query("UPDATE watchlists SET isDeleted = 1, isSynced = 0 WHERE id = :watchlistId")
    suspend fun markAsDeleted(watchlistId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: WatchlistEntity)

    // ✅ New: clear all watchlists (for new user)
    @Query("DELETE FROM watchlists")
    suspend fun clearAllWatchlists()

    // ✅ New: reassign watchlists to a new userId (after backend ID received)
    @Query("UPDATE watchlists SET userId = :newUserId WHERE userId = :oldUserId")
    suspend fun reassignWatchlistsUser(oldUserId: String, newUserId: String)
}
