package com.stocksentry.app.data.models

data class NewsResponse(
    val status: String? = null,
    val totalResults: Int = 0,
    val articles: List<Article> = emptyList()
)

data class Article(
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val url: String = "",
    val publishedAt: String = "",
    val sourceUrl: String = ""
)

data class Source(
    val name: String = "",
    val id: String? = null
)

