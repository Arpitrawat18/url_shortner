package com.Project.URL_Shortner.Controller;

import com.Project.URL_Shortner.Service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode:[A-Za-z0-9]{6}}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        log.info("Received public redirect request for short code: {}", shortCode);
        String originalUrl = urlService.getOriginalUrl(shortCode, request);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
