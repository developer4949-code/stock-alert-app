package com.example.stocksentry.repository;


import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class OtpRepository {

    private final DynamoDbClient dynamoDbClient;

    public OtpRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveOtp(String otp, String watchlistId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("otp", AttributeValue.builder().s(otp).build());
        item.put("watchlistId", AttributeValue.builder().s(watchlistId).build());
        item.put("expiry", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond() + 600)).build()); // 10 min expiry

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Otps")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public String getWatchlistIdByOtp(String otp) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName("Otps")
                .key(Map.of("otp", AttributeValue.builder().s(otp).build()))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if (item != null && Long.parseLong(item.get("expiry").n()) > Instant.now().getEpochSecond()) {
            return item.get("watchlistId").s();
        }
        return null;  // OTP invalid or expired
    }
}
