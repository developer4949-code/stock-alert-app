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
        watchlistService.createWatchlist(watchlist.getId(), watchlist.getUserId(), watchlist.getName(), watchlist.getSymbols());
    }

    @PostMapping("/upsert")
    public void upsertWatchlist(@RequestBody Watchlist watchlist) {
        watchlistService.upsertWatchlist(watchlist);
    }

    @GetMapping("/{userId}")
    public List<Watchlist> getWatchlists(@PathVariable String userId) {
        return watchlistService.getWatchlists(userId);
    }

    @DeleteMapping("/{watchlistId}")
    public void deleteWatchlist(@PathVariable String watchlistId) {
        watchlistService.deleteWatchlist(watchlistId);
    }

    @PostMapping("/{watchlistId}/symbols")
    public void addSymbols(@PathVariable String watchlistId, @RequestBody List<String> symbols) {
        watchlistService.addSymbols(watchlistId, symbols);
    }

    @DeleteMapping("/{watchlistId}/symbols/{symbol}")
    public void removeSymbol(@PathVariable String watchlistId, @PathVariable String symbol) {
        watchlistService.removeSymbol(watchlistId, symbol);
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
