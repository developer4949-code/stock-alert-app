package com.example.stocksentry.dto;

import lombok.Data;
import java.util.List;

@Data  // Lombok for getters/setters
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    @Data
    public static class Article {
        private String title;
        private String description;
        private String content;
        private String publishedAt;  // publish date from API
        private String url;          // article URL


    }
}
