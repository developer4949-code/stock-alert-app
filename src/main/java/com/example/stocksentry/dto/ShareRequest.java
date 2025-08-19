package com.example.stocksentry.dto;

import lombok.Data;

@Data
public class ShareRequest {
    private String watchlistId;
    private String recipientEmail;
}
