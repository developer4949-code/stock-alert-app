package com.example.stocksentry.service;


import com.example.stocksentry.dto.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsScheduler {

    @Autowired
    private NewsService newsService;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void checkNewsForAllWatchlists() {
        try {
            // Fetch all user IDs dynamically
            List<String> userIds = userService.getAllUserIds();
            
            for (String userId : userIds) {
                try {
                    List<Watchlist> watchlists = watchlistService.getWatchlists(userId);
                    for (Watchlist watchlist : watchlists) {
                        for (String symbol : watchlist.getSymbols()) {
                            try {
                                newsService.checkAndNotifyAlerts(symbol);
                            } catch (Exception e) {
                                System.err.println("Error checking alerts for symbol " + symbol + ": " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing watchlists for user " + userId + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in scheduled news check: " + e.getMessage());
        }
    }
}
