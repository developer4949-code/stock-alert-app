package com.example.stocksentry.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.Random;

@Service
public class OtpService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public OtpService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // 6-digit OTP
    }

    public void sendOtp(String recipientEmail, String otp) {
        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(recipientEmail).build())
                .message(Message.builder()
                        .subject(Content.builder().data("StockSentry Watchlist OTP").build())
                        .body(Body.builder()
                                .text(Content.builder().data("Your OTP is: " + otp).build())
                                .build())
                        .build())
                .source(fromEmail)
                .build();

        sesClient.sendEmail(request);
    }
}
