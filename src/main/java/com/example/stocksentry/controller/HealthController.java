package com.example.stocksentry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.Instant.now().toString());
        
        // Check AWS DynamoDB connection
        try {
            dynamoDbClient.listTables(ListTablesRequest.builder().build());
            health.put("aws", "CONNECTED");
        } catch (Exception e) {
            health.put("aws", "ERROR: " + e.getMessage());
            health.put("status", "DOWN");
        }
        
        return health;
    }
}
