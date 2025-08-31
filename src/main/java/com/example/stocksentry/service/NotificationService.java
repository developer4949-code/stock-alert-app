package com.example.stocksentry.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class NotificationService {

    private final SnsClient snsClient;
    private final SesClient sesClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public NotificationService(SnsClient snsClient, SesClient sesClient) {
        this.snsClient = snsClient;
        this.sesClient = sesClient;
    }

    public void sendAlert(String symbol, String message) {
        // Send to SNS topic (existing functionality)
        PublishRequest snsRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message("Alert for " + symbol + ": " + message)
                .subject("StockSentry Alert")
                .build();
        snsClient.publish(snsRequest);
    }

    public void sendEmailAlert(String userEmail, String symbol, String message) {
        SendEmailRequest sesRequest = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(userEmail).build())
                .message(Message.builder()
                        .subject(Content.builder().data("StockSentry Alert: " + symbol).build())
                        .body(Body.builder()
                                .text(Content.builder().data("Alert for " + symbol + ": " + message).build())
                                .build())
                        .build())
                .source(fromEmail)
                .build();

        sesClient.sendEmail(sesRequest);
    }

    public void sendSmsAlert(String phoneNumber, String symbol, String message) {
        PublishRequest smsRequest = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message("StockSentry Alert: " + symbol + " - " + message)
                .build();
        snsClient.publish(smsRequest);
    }
}
