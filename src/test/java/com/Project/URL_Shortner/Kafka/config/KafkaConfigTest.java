package com.Project.URL_Shortner.Kafka.config;

import com.Project.URL_Shortner.Kafka.event.AnalyticsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
    }

    private void withBootstrapServers(String value) {
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", value);
    }

    @Test
    void producerFactory_usesConfiguredBootstrapServers() {
        withBootstrapServers("localhost:9092");

        ProducerFactory<String, AnalyticsEvent> factory = kafkaConfig.producerFactory();

        assertThat(factory.getConfigurationProperties())
                .containsEntry("bootstrap.servers", "localhost:9092");
    }

    @Test
    void producerFactory_supportsDockerBootstrapServers() {
        withBootstrapServers("kafka:9092");

        ProducerFactory<String, AnalyticsEvent> factory = kafkaConfig.producerFactory();

        assertThat(factory.getConfigurationProperties())
                .containsEntry("bootstrap.servers", "kafka:9092");
    }

    @Test
    void consumerFactory_usesConfiguredBootstrapServers() {
        withBootstrapServers("localhost:9092");

        ConsumerFactory<String, AnalyticsEvent> factory = kafkaConfig.consumerFactory();

        assertThat(factory.getConfigurationProperties())
                .containsEntry("bootstrap.servers", "localhost:9092");
    }

    @Test
    void consumerFactory_supportsDockerBootstrapServers() {
        withBootstrapServers("kafka:9092");

        ConsumerFactory<String, AnalyticsEvent> factory = kafkaConfig.consumerFactory();

        assertThat(factory.getConfigurationProperties())
                .containsEntry("bootstrap.servers", "kafka:9092");
    }

    @Test
    void kafkaTemplate_isCreatedWithProducerFactory() {
        withBootstrapServers("localhost:9092");

        assertThat(kafkaConfig.kafkaTemplate()).isNotNull();
    }
}
