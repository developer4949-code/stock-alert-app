package com.example.stocksentry.controller;


import com.example.stocksentry.dto.NewsResponse;
import com.example.stocksentry.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/{symbol}")
    public NewsResponse getNews(@PathVariable String symbol) {
        return newsService.fetchNewsForStock(symbol);
    }
}
