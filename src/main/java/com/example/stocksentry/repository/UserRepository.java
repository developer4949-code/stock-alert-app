package com.example.stocksentry.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {

    public final DynamoDbClient dynamoDbClient;

    public UserRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public String createUser(String name, String email, String phoneNumber) {
        String userId = UUID.randomUUID().toString();
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.builder().s(userId).build());
        item.put("name", AttributeValue.builder().s(name).build());
        item.put("email", AttributeValue.builder().s(email).build());
        item.put("phoneNumber", AttributeValue.builder().s(phoneNumber).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("Users")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        return userId;
    }

    public Map<String, AttributeValue> getUserById(String userId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName("Users")
                .key(Map.of("userId", AttributeValue.builder().s(userId).build()))
                .build();
        return dynamoDbClient.getItem(request).item();
    }
}
