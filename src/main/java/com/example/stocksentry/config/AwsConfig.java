package com.example.stocksentry.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.AP_SOUTH_1) // Mumbai
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("AKIAY67PQ7R4YNKSHUV4", "ufnHLKifKo1lqlF2GcIqXUShsvtHhGqrjkJbqbnc")
                ))
                .build();
    }


    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.AP_SOUTH_1) // change to your region
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.AP_SOUTH_1) // change if needed
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                "AKIAY67PQ7R4YNKSHUV4",
                                "ufnHLKifKo1lqlF2GcIqXUShsvtHhGqrjkJbqbnc"
                        )
                ))
                .build();
    }
}
