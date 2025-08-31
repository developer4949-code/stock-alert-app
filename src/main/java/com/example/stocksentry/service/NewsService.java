package com.example.stocksentry.service;


import com.example.stocksentry.dto.NewsResponse;
import com.example.stocksentry.repository.AlertLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NewsService {

    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertLogRepository alertLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WatchlistService watchlistService;

    public NewsResponse fetchNewsForStock(String symbol) {
        String url = "https://newsapi.org/v2/everything?q=" + symbol + "&apiKey=" + apiKey;
        return restTemplate.getForObject(url, NewsResponse.class);
    }

    public boolean shouldTriggerAlert(NewsResponse response) {
        if (response != null && response.getArticles() != null) {
            for (NewsResponse.Article article : response.getArticles()) {
                String text = (article.getTitle() + " " + article.getDescription()).toLowerCase();
                if (text.contains("earnings") || text.contains("acquisition") || text.contains("merger")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkAndNotifyAlerts(String symbol) {
        NewsResponse response = fetchNewsForStock(symbol);
        if (shouldTriggerAlert(response)) {
            String message = "News alert for " + symbol + ": Significant event detected";
            
            // Send to SNS topic (existing functionality)
            notificationService.sendAlert(symbol, message);
            
            // Send personalized email alerts to users who have this stock in their watchlist
            sendPersonalizedAlerts(symbol, message);
            
            alertLogRepository.logAlert(symbol, message);
        }
    }

    private void sendPersonalizedAlerts(String symbol, String message) {
        // Get all users
        List<String> userIds = userService.getAllUserIds();
        
        for (String userId : userIds) {
            try {
                // Get user details
                var user = userService.getUser(userId);
                
                // Get user's watchlists
                var watchlists = watchlistService.getWatchlists(userId);
                
                // Check if user has this symbol in any of their watchlists
                boolean hasSymbol = watchlists.stream()
                        .anyMatch(watchlist -> watchlist.getSymbols().contains(symbol));
                
                // If user has this symbol, send notifications
                if (hasSymbol) {
                    // Send email alert if email is available
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        notificationService.sendEmailAlert(user.getEmail(), symbol, message);
                    }
                    
                    // Send SMS alert if phone number is available
                    if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                        notificationService.sendSmsAlert(user.getPhoneNumber(), symbol, message);
                    }
                }
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Error sending alert to user " + userId + ": " + e.getMessage());
            }
        }
    }
}
