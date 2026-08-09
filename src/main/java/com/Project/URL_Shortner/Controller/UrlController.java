package com.Project.URL_Shortner.Controller;

import com.Project.URL_Shortner.Dto.request.CreateUrlRequest;
import com.Project.URL_Shortner.Dto.response.AnalyticsResponse;
import com.Project.URL_Shortner.Dto.response.UrlResponse;
import com.Project.URL_Shortner.Service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {
    private final UrlService urlService;
    
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        log.info("Received request to create short URL for: {}", request.getOriginalUrl());
        UrlResponse urlResponse = urlService.createShortUrl(request);
        log.info("Short URL created successfully: {}", urlResponse.getShortCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponse);
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String shortCode, HttpServletRequest request) {
        log.info("Received redirect request for short code: {}", shortCode);
        String originalUrl = urlService.getOriginalUrl(shortCode, request);
        log.debug("Redirecting {} to {}", shortCode, originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
    
    @GetMapping("/myurls")
    public ResponseEntity<List<UrlResponse>> getMyUrls() {
        log.info("Received request to fetch user's URLs");
        List<UrlResponse> urls = urlService.getMyUrls();
        log.info("Fetched {} URLs for user", urls.size());
        return ResponseEntity.ok(urls);
    }
    
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        log.info("Received request to delete short URL: {}", shortCode);
        urlService.deleteUrl(shortCode);
        log.info("Short URL deleted successfully: {}", shortCode);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{shortCode}/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        log.info("Received request for analytics of short code: {}", shortCode);
        AnalyticsResponse analytics = urlService.getAnalytics(shortCode);
        log.debug("Analytics retrieved for {}: {} total clicks", shortCode, analytics.getTotalClicks());
        return ResponseEntity.ok(analytics);
    }
}
