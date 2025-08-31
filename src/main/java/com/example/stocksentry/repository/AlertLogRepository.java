package com.example.stocksentry.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class AlertLogRepository {

    private final DynamoDbClient dynamoDbClient;

    public AlertLogRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void logAlert(String symbol, String message) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
            item.put("symbol", AttributeValue.builder().s(symbol).build());
            item.put("message", AttributeValue.builder().s(message).build());
            item.put("timestamp", AttributeValue.builder().s(java.time.Instant.now().toString()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("AlertLogs")
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Error logging alert to DynamoDB: " + e.getMessage());
        }
    }
}
