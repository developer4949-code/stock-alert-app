package com.example.stocksentry.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class AlertLogRepository {

    private final DynamoDbClient dynamoDbClient;

    @Value("${aws.region}")
    private String region;

    public AlertLogRepository() {
        this.dynamoDbClient = DynamoDbClient.builder().build();  // Use default credentials chain
    }

    public void logAlert(String symbol, String message) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("symbol", AttributeValue.builder().s(symbol).build());
        item.put("message", AttributeValue.builder().s(message).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("AlertLogs")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
}
