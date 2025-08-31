package com.example.stocksentry.controller;


import com.example.stocksentry.dto.NewsResponse;
import com.example.stocksentry.service.NewsService;
import com.example.stocksentry.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{symbol}")
    public NewsResponse getNews(@PathVariable String symbol) {
        return newsService.fetchNewsForStock(symbol);
    }

    @GetMapping("/test-sns/{symbol}")
    public String testSnsNotification(@PathVariable String symbol) {
        try {
            notificationService.sendAlert(symbol, "This is a test notification for " + symbol);
            return "SNS notification sent successfully for " + symbol;
        } catch (Exception e) {
            return "Error sending SNS notification: " + e.getMessage();
        }
    }

    @GetMapping("/test-sms/{phoneNumber}/{symbol}")
    public String testSmsNotification(@PathVariable String phoneNumber, @PathVariable String symbol) {
        try {
            notificationService.sendSmsAlert(phoneNumber, symbol, "This is a test SMS notification for " + symbol);
            return "SMS notification sent successfully to " + phoneNumber + " for " + symbol;
        } catch (Exception e) {
            return "Error sending SMS notification: " + e.getMessage();
        }
    }
}
