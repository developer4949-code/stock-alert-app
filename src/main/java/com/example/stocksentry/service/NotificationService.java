package com.example.stocksentry.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class NotificationService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public NotificationService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void sendAlert(String symbol, String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message("Alert for " + symbol + ": " + message)
                .subject("StockSentry Alert")
                .build();
        snsClient.publish(request);
    }
}
