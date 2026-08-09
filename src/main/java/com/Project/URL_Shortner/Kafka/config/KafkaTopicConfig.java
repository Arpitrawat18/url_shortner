package com.Project.URL_Shortner.Kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String ANALYTICS_TOPIC = "analytics-topic";

    @Bean
    public NewTopic analyticsTopic() {
        return new NewTopic(ANALYTICS_TOPIC, 1, (short) 1);
    }
}