package com.example.stocksentry.service;



import com.example.stocksentry.dto.NewsResponse;
import com.example.stocksentry.repository.AlertLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsService {

    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertLogRepository alertLogRepository;

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
            notificationService.sendAlert(symbol, message);
            alertLogRepository.logAlert(symbol, message);
        }
    }
}
