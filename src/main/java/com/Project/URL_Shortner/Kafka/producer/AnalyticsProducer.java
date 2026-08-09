package com.Project.URL_Shortner.Kafka.producer;

import com.Project.URL_Shortner.Kafka.config.KafkaTopicConfig;
import com.Project.URL_Shortner.Kafka.event.AnalyticsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsProducer {

    private final KafkaTemplate<String, AnalyticsEvent> kafkaTemplate;

    public void sendAnalyticsEvent(AnalyticsEvent event) {

        kafkaTemplate.send(KafkaTopicConfig.ANALYTICS_TOPIC, event);

        log.info("Analytics event published for URL ID: {}", event.getUrlId());
    }
}