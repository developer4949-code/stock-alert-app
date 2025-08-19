package com.example.stocksentry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class StockSentryApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockSentryApplication.class, args);
    }

}
