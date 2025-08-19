package com.example.stocksentry.exception;


public class StockSentryException extends RuntimeException {
    public StockSentryException(String message) {
        super(message);
    }

    public StockSentryException(String message, Throwable cause) {
        super(message, cause);
    }
}
