package com.Project.URL_Shortner.Dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class    CreateUrlRequest {
    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Please enter a valid URL")
    private String originalUrl;
    private Integer expiresAt;
}
