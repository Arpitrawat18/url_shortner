package com.Project.URL_Shortner.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private String originalUrl;

    private String shortCode;

    private Long totalClicks;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Long uniqueVisitors;

    private String topBrowser;

    private String topDevice;

    private String topOperatingSystem;

    private String topReferrer;
}