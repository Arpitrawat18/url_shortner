package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Dto.request.CreateUrlRequest;
import com.Project.URL_Shortner.Dto.response.AnalyticsResponse;
import com.Project.URL_Shortner.Dto.response.UrlResponse;
import com.Project.URL_Shortner.Entities.AnalyticsEntity;
import com.Project.URL_Shortner.Entities.UrlEntity;
import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Exception.UrlExpiredException;
import com.Project.URL_Shortner.Exception.UrlNotFoundException;
import com.Project.URL_Shortner.Kafka.event.AnalyticsEvent;
import com.Project.URL_Shortner.Kafka.producer.AnalyticsProducer;
import com.Project.URL_Shortner.Repository.AnalyticsRepository;
import com.Project.URL_Shortner.Repository.UrlRepository;
import com.Project.URL_Shortner.Service.UrlService;
import com.Project.URL_Shortner.Utils.ShortCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service

@RequiredArgsConstructor
@Slf4j


public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisTemplate<String, UrlEntity> redisTemplate;
    private final AnalyticsRepository analyticsRepository;
    private final UserAgentAnalyzer userAgentAnalyzer;
    private final AnalyticsProducer analyticsProducer;

    @Value("${app.base-url}")
    private String baseUrl;

    //URL CODE GENERATE

    @Override
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        String shortCode;
        Integer expiry = request.getExpiresAt();
        if(expiry==null){
            expiry=7; // Set default expiry to 7 days from now
        }
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiry);

        do {
            shortCode = shortCodeGenerator.generateShortCode();}
        while (urlRepository.existsByShortCode(shortCode));

        // Authentication

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = null;

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserEntity) {

            user = (UserEntity) authentication.getPrincipal();
        }



        // Create entity
        log.info("Creating short URL for {}", request.getOriginalUrl());
        UrlEntity urlEntity=UrlEntity.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .expiresAt(expiresAt)
                .user(user)
                .build();


        // Save entity
        UrlEntity savedUrlEntity = urlRepository.save(urlEntity);
        log.info("Short URL {} created successfully", savedUrlEntity.getShortCode());
        if (user != null) {
            log.info("User {} created short URL {}", user.getEmail(), shortCode);
        } else {
            log.info("Guest created short URL {}", shortCode);
        }


        // Return response
        return UrlResponse.builder()
                .id(savedUrlEntity.getId())
                .originalUrl(savedUrlEntity.getOriginalUrl())
                .shortCode(savedUrlEntity.getShortCode())
                .shortUrl(baseUrl + "/" + savedUrlEntity.getShortCode())
                .createdAt(savedUrlEntity.getCreatedAt())
                .expiresAt(savedUrlEntity.getExpiresAt())
                .build();
    }

    //ORIGINAL URL

    @Override
    public String getOriginalUrl(String shortCode, HttpServletRequest request)
    {
        log.info("Redirect request received for {}", shortCode);

        // Find URL (Redis is optional - fall back to DB if unavailable)
        UrlEntity urlEntity = null;
        try {
            urlEntity = redisTemplate.opsForValue().get(shortCode);
        } catch (Exception e) {
            log.warn("Redis unavailable during lookup for {} - falling back to database: {}", shortCode, e.getMessage());
        }
        if (urlEntity != null) {
            log.info("Redis cache hit for {}", shortCode);
        } else {
            log.info("Redis cache miss for {}", shortCode);
            urlEntity = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));

            try {
                redisTemplate.opsForValue().set(
                        shortCode,
                        urlEntity,
                        Duration.ofMinutes(1)
                );
            } catch (Exception e) {
                log.warn("Redis unavailable during cache write for {}: {}", shortCode, e.getMessage());
            }
        }
        if (urlEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            try {
                redisTemplate.delete(shortCode);
            } catch (Exception e) {
                log.warn("Redis unavailable during cache eviction for {}: {}", shortCode, e.getMessage());
            }
            throw new UrlExpiredException("This URL has expired");
        }
        String userAgent = request.getHeader("User-Agent");

        UserAgent agent = userAgentAnalyzer.parse(userAgent);

        AnalyticsEvent event = AnalyticsEvent.builder()
                .urlId(urlEntity.getId())
                .clickedAt(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .userAgent(userAgent)
                .browser(agent.getValue("AgentName"))
                .device(agent.getValue("DeviceClass"))
                .operatingSystem(agent.getValue("OperatingSystemName"))
                .referrer(request.getHeader("Referer"))
                .build();

        analyticsProducer.sendAnalyticsEvent(event);
        log.info("Analytics recorded for {}", shortCode);
        log.info("Redirecting {} to {}", shortCode, urlEntity.getOriginalUrl());

        return urlEntity.getOriginalUrl();
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        if (!(authentication.getPrincipal() instanceof UserEntity loggedInUser)) {
            throw new AccessDeniedException("Invalid authentication principal");
        }

        return loggedInUser;
    }

    @Override
    public List<UrlResponse> getMyUrls() {
        log.info("Fetching URLs for authenticated user");

        UserEntity loggedInUser = getAuthenticatedUser();

        log.debug("Fetching URLs for user ID: {}", loggedInUser.getId());
        List<UrlEntity> urls = urlRepository.findByUser_Id(loggedInUser.getId());

        log.info("Retrieved {} URLs for user: {}", urls.size(), loggedInUser.getEmail());
        return urls.stream()
                .map(url -> UrlResponse.builder()
                        .id(url.getId())
                        .originalUrl(url.getOriginalUrl())
                        .shortCode(url.getShortCode())
                        .shortUrl(baseUrl + "/" + url.getShortCode())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .build())
                .toList();
    }

    @Override
    public void deleteUrl(String shortCode) {
        log.info("Delete request for short URL: {}", shortCode);

        UserEntity loggedInUser = getAuthenticatedUser();

        UrlEntity url = urlRepository.findByShortCodeAndUser_Id(shortCode, loggedInUser.getId())
                .orElseThrow(() -> {
                    log.warn("Short URL not found or not owned for deletion: {} by user: {}", shortCode, loggedInUser.getEmail());
                    return new UrlNotFoundException("Short URL not found");
                });

        try {
            redisTemplate.delete(shortCode);
        } catch (Exception e) {
            log.warn("Redis unavailable while evicting cached URL {} - continuing with deletion: {}", shortCode, e.getMessage());
        }
        urlRepository.delete(url);
        log.info("Short URL deleted successfully: {} by user: {}", shortCode, loggedInUser.getEmail());
    }

    //ANALYTICS

    @Override
    public AnalyticsResponse getAnalytics(String shortCode) {
        log.info("Analytics request for short URL: {}", shortCode);

        UserEntity loggedInUser = getAuthenticatedUser();

        UrlEntity url = urlRepository.findByShortCodeAndUser_Id(shortCode, loggedInUser.getId())
                .orElseThrow(() -> {
                    log.warn("Short URL not found or not owned for analytics: {} by user: {}", shortCode, loggedInUser.getEmail());
                    return new UrlNotFoundException("Short URL not found");
                });

        long totalClicks = analyticsRepository.countByUrl(url);

        AnalyticsResponse response = AnalyticsResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .totalClicks(totalClicks)
                .uniqueVisitors(analyticsRepository.countDistinctIpAddressByUrl(url))
                .topBrowser(analyticsRepository.findTopBrowser(url.getId()))
                .topDevice(analyticsRepository.findTopDevice(url.getId()))
                .topOperatingSystem(analyticsRepository.findTopOperatingSystem(url.getId()))
                .topReferrer(analyticsRepository.findTopReferrer(url.getId()))
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();

        log.debug("Analytics retrieved for {}: {} total clicks, {} unique visitors",
                shortCode, totalClicks, response.getUniqueVisitors());
        return response;
    }


}
