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

    public void saveWatchlist(String id, String userId, String name, List<String> symbols) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id != null && !id.isEmpty() ? id : UUID.randomUUID().toString()).build());
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

    public void upsertWatchlist(String id, String userId, String name, List<String> symbols) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id != null && !id.isEmpty() ? id : UUID.randomUUID().toString()).build());
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

    public void deleteWatchlistById(String watchlistId) {
        // Scan to find item by id to get its userId (partition key)
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Watchlists")
                .filterExpression("id = :id")
                .expressionAttributeValues(Map.of(
                        ":id", AttributeValue.builder().s(watchlistId).build()
                ))
                .limit(1)
                .build();

        List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scanRequest).items();
        if (items.isEmpty()) {
            return;
        }
        Map<String, AttributeValue> item = items.get(0);
        String userId = item.get("userId").s();

        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName("Watchlists")
                .key(Map.of(
                        "userId", AttributeValue.builder().s(userId).build(),
                        "id", AttributeValue.builder().s(watchlistId).build()
                ))
                .build();

        dynamoDbClient.deleteItem(deleteRequest);
    }

    public void addSymbols(String watchlistId, List<String> symbolsToAdd) {
        // Fetch existing item
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Watchlists")
                .filterExpression("id = :id")
                .expressionAttributeValues(Map.of(
                        ":id", AttributeValue.builder().s(watchlistId).build()
                ))
                .limit(1)
                .build();

        List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scanRequest).items();
        if (items.isEmpty()) {
            return;
        }
        Map<String, AttributeValue> item = items.get(0);
        String userId = item.get("userId").s();
        List<AttributeValue> existing = item.getOrDefault("symbols", AttributeValue.builder().l(new ArrayList<>()).build()).l();

        // Merge and de-duplicate
        Set<String> merged = new LinkedHashSet<>();
        for (AttributeValue v : existing) merged.add(v.s());
        merged.addAll(symbolsToAdd);

        List<AttributeValue> updated = merged.stream()
                .map(s -> AttributeValue.builder().s(s).build())
                .toList();

        UpdateItemRequest update = UpdateItemRequest.builder()
                .tableName("Watchlists")
                .key(Map.of(
                        "userId", AttributeValue.builder().s(userId).build(),
                        "id", AttributeValue.builder().s(watchlistId).build()
                ))
                .updateExpression("SET symbols = :symbols")
                .expressionAttributeValues(Map.of(
                        ":symbols", AttributeValue.builder().l(updated).build()
                ))
                .build();

        dynamoDbClient.updateItem(update);
    }

    public void removeSymbol(String watchlistId, String symbol) {
        // Fetch existing item
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Watchlists")
                .filterExpression("id = :id")
                .expressionAttributeValues(Map.of(
                        ":id", AttributeValue.builder().s(watchlistId).build()
                ))
                .limit(1)
                .build();

        List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scanRequest).items();
        if (items.isEmpty()) {
            return;
        }
        Map<String, AttributeValue> item = items.get(0);
        String userId = item.get("userId").s();
        List<AttributeValue> existing = item.getOrDefault("symbols", AttributeValue.builder().l(new ArrayList<>()).build()).l();

        List<AttributeValue> updated = existing.stream()
                .map(AttributeValue::s)
                .filter(s -> !s.equals(symbol))
                .map(s -> AttributeValue.builder().s(s).build())
                .toList();

        UpdateItemRequest update = UpdateItemRequest.builder()
                .tableName("Watchlists")
                .key(Map.of(
                        "userId", AttributeValue.builder().s(userId).build(),
                        "id", AttributeValue.builder().s(watchlistId).build()
                ))
                .updateExpression("SET symbols = :symbols")
                .expressionAttributeValues(Map.of(
                        ":symbols", AttributeValue.builder().l(updated).build()
                ))
                .build();

        dynamoDbClient.updateItem(update);
    }
}
