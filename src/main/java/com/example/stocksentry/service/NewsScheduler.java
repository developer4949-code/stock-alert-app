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
        // Fetch all user IDs dynamically (e.g., from UserService or a scan of Users table)
        // For simplicity, assume UserService can provide a list of userIds
        List<String> userIds = userService.getAllUserIds(); // Implement this method in UserService
        for (String userId : userIds) {
            List<Watchlist> watchlists = watchlistService.getWatchlists(userId);
            for (Watchlist watchlist : watchlists) {
                for (String symbol : watchlist.getSymbols()) {
                    newsService.checkAndNotifyAlerts(symbol);
                }
            }
        }
    }
}
