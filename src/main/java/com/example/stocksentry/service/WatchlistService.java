package com.example.stocksentry.service;


import com.example.stocksentry.dto.Watchlist;
import com.example.stocksentry.exception.StockSentryException;
import com.example.stocksentry.repository.OtpRepository;
import com.example.stocksentry.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final OtpService otpService;
    private final OtpRepository otpRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, OtpService otpService, OtpRepository otpRepository) {
        this.watchlistRepository = watchlistRepository;
        this.otpService = otpService;
        this.otpRepository = otpRepository;
    }

    public void createWatchlist(String userId, String name, List<String> symbols) {
        watchlistRepository.saveWatchlist(userId, name, symbols);
    }

    public List<Watchlist> getWatchlists(String userId) {
        return watchlistRepository.getWatchlistsByUser(userId).stream()
                .map(item -> {
                    Watchlist watchlist = new Watchlist();
                    watchlist.setId(item.get("id").s());
                    watchlist.setUserId(item.get("userId").s());
                    watchlist.setName(item.get("name").s());
                    watchlist.setSymbols(item.get("symbols").l().stream()
                            .map(AttributeValue::s)
                            .collect(Collectors.toList()));
                    return watchlist;
                })
                .collect(Collectors.toList());
    }

    public Watchlist getWatchlistById(String userId, String watchlistId) {
        Map<String, AttributeValue> item = watchlistRepository.getWatchlistById(userId, watchlistId);
        if (item == null) {
            throw new StockSentryException("Watchlist not found for ID: " + watchlistId);
        }
        Watchlist watchlist = new Watchlist();
        watchlist.setId(item.get("id").s());
        watchlist.setUserId(item.get("userId").s());
        watchlist.setName(item.get("name").s());
        watchlist.setSymbols(item.get("symbols").l().stream()
                .map(AttributeValue::s)
                .collect(Collectors.toList()));
        return watchlist;
    }

    public String shareWatchlist(String watchlistId, String recipientEmail) {
        String otp = otpService.generateOtp();
        otpRepository.saveOtp(otp, watchlistId);
        otpService.sendOtp(recipientEmail, otp);
        return "stocksentry://share/" + otp;
    }
}
