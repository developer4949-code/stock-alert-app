package com.stocksentry.app.data.remote

import com.stocksentry.app.data.local.WatchlistEntity
import com.stocksentry.app.data.models.User
import com.stocksentry.app.data.models.Watchlist
import com.stocksentry.app.data.models.NewsResponse
import com.stocksentry.app.data.models.ShareRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StockSentryApi {
    @POST("user")
    suspend fun createUser(@Body user: User): String // Returns userId

    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId: String): User

    @POST("watchlist")
    suspend fun createWatchlist(@Body watchlist: Watchlist): Unit

    @POST("watchlist/upsert")
    suspend fun upsertWatchlist(@Body watchlist: Watchlist): Unit

    @GET("watchlist/{userId}")
    suspend fun getWatchlists(@Path("userId") userId: String): List<Watchlist>

    @DELETE("watchlist/{watchlistId}")
    suspend fun deleteWatchlist(@Path("watchlistId") watchlistId: String): Unit

    @GET("watchlist/share/{otp}")
    suspend fun getSharedWatchlist(@Path("otp") otp: String, @Query("userId") userId: String): Watchlist

    @POST("watchlist/share")
    suspend fun shareWatchlist(@Body shareRequest: ShareRequest): String // Returns deep link

    @GET("news/{symbol}")
    suspend fun getNews(@Path("symbol") symbol: String): NewsResponse

    @GET("health")
    suspend fun healthCheck(): String
}

