package com.Project.URL_Shortner.Kafka.consumer;

import com.Project.URL_Shortner.Entities.AnalyticsEntity;
import com.Project.URL_Shortner.Entities.UrlEntity;
import com.Project.URL_Shortner.Kafka.event.AnalyticsEvent;
import com.Project.URL_Shortner.Repository.AnalyticsRepository;
import com.Project.URL_Shortner.Repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {

    private final UrlRepository urlRepository;
    private final AnalyticsRepository analyticsRepository;

    @KafkaListener(
            topics = "analytics-topic",
            groupId = "analytics-group"
    )
    public void consume(AnalyticsEvent event) {

        UrlEntity url = urlRepository.findById(event.getUrlId())
                .orElse(null);

        if (url == null) {
            log.warn("URL not found for analytics event: {}", event.getUrlId());
            return;
        }

        AnalyticsEntity analytics = AnalyticsEntity.builder()
                .url(url)
                .clickedAt(event.getClickedAt())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .browser(event.getBrowser())
                .device(event.getDevice())
                .operatingSystem(event.getOperatingSystem())
                .referrer(event.getReferrer())
                .build();

        analyticsRepository.save(analytics);

        log.info("Analytics saved for URL ID: {}", event.getUrlId());
    }
}