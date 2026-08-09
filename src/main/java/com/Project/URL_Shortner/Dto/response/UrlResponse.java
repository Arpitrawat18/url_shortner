package com.Project.URL_Shortner.Dto.response;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class UrlResponse {
        private Long id;
        private String originalUrl;
        private String shortCode;
        private String shortUrl;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
    }

