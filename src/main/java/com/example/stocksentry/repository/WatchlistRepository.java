package com.example.stocksentry.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Repository
public class WatchlistRepository {

    private final DynamoDbClient dynamoDbClient;

    public WatchlistRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveWatchlist(String userId, String name, List<String> symbols) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("userId", AttributeValue.builder().s(userId).build());
        item.put("name", AttributeValue.builder().s(name).build());
        item.put("symbols", AttributeValue.builder().l(
                symbols.stream()
                        .map(s -> AttributeValue.builder().s(s).build())
                        .toList()
        ).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Watchlists")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public List<Map<String, AttributeValue>> getWatchlistsByUser(String userId) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":userId", AttributeValue.builder().s(userId).build());

        QueryRequest request = QueryRequest.builder()
                .tableName("Watchlists")
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(expressionValues)
                .build();

        return dynamoDbClient.query(request).items();
    }

    public Map<String, AttributeValue> getWatchlistById(String userId, String watchlistId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName("Watchlists")
                .key(Map.of(
                        "userId", AttributeValue.builder().s(userId).build(),
                        "id", AttributeValue.builder().s(watchlistId).build()
                ))
                .build();

        return dynamoDbClient.getItem(request).item();
    }
}
