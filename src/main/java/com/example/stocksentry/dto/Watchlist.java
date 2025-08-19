package com.example.stocksentry.dto;


import lombok.Data;
import java.util.List;

@Data
public class Watchlist {
    private String id;  // Unique watchlist ID
    private String userId;  // To associate with a user
    private String name;  // Watchlist name (e.g., "My Tech Stocks")
    private List<String> symbols;  // List of stock symbols (e.g., ["AAPL", "GOOGL"])
}
