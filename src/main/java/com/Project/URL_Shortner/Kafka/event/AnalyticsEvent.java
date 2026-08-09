package com.Project.URL_Shortner.Kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {

    private Long urlId;

    private LocalDateTime clickedAt;

    private String ipAddress;

    private String userAgent;

    private String browser;

    private String device;

    private String operatingSystem;

    private String referrer;
}