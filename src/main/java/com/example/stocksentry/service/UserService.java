package com.example.stocksentry.service;




import com.example.stocksentry.dto.User;
import com.example.stocksentry.exception.StockSentryException;
import com.example.stocksentry.repository.UserRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createUser(String name, String email, String phoneNumber) {
        return userRepository.createUser(name, email, phoneNumber);
    }

    public User getUser(String userId) {
        Map<String, AttributeValue> item = userRepository.getUserById(userId);
        if (item == null) {
            throw new StockSentryException("User not found for ID: " + userId);
        }
        User user = new User();
        user.setUserId(item.get("userId").s());
        user.setName(item.get("name").s());
        user.setEmail(item.get("email").s());
        user.setPhoneNumber(item.get("phoneNumber").s());
        return user;
    }


    public List<String> getAllUserIds() {
        // Use ScanRequest to fetch all items from Users table
        software.amazon.awssdk.services.dynamodb.model.ScanRequest scanRequest = software.amazon.awssdk.services.dynamodb.model.ScanRequest.builder()
                .tableName("Users")
                .build();

        List<Map<String, AttributeValue>> items = userRepository.dynamoDbClient.scan(scanRequest).items();
        List<String> userIds = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            userIds.add(item.get("userId").s());
        }
        return userIds;
    }
}
