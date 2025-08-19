package com.example.stocksentry.controller;




import com.example.stocksentry.dto.ShareRequest;
import com.example.stocksentry.dto.Watchlist;

import com.example.stocksentry.exception.StockSentryException;
import com.example.stocksentry.repository.OtpRepository;
import com.example.stocksentry.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private OtpRepository otpRepository;

    @PostMapping
    public void createWatchlist(@RequestBody Watchlist watchlist) {
        watchlistService.createWatchlist(watchlist.getUserId(), watchlist.getName(), watchlist.getSymbols());
    }

    @GetMapping("/{userId}")
    public List<Watchlist> getWatchlists(@PathVariable String userId) {
        return watchlistService.getWatchlists(userId);
    }

    @PostMapping("/share")
    public String shareWatchlist(@RequestBody ShareRequest shareRequest) {
        return watchlistService.shareWatchlist(shareRequest.getWatchlistId(), shareRequest.getRecipientEmail());
    }

    @GetMapping("/share/{otp}")
    public Watchlist getSharedWatchlist(@PathVariable String otp, @RequestParam String userId) {
        String watchlistId = otpRepository.getWatchlistIdByOtp(otp);
        if (watchlistId == null) {
            throw new StockSentryException("Invalid or expired OTP");
        }
        return watchlistService.getWatchlistById(userId, watchlistId);
    }
}
