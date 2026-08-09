package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Dto.request.CreateUrlRequest;
import com.Project.URL_Shortner.Dto.response.UrlResponse;
import com.Project.URL_Shortner.Entities.UrlEntity;
import com.Project.URL_Shortner.Repository.UrlRepository;
import com.Project.URL_Shortner.Utils.ShortCodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private UrlServiceImpl serviceWithBaseUrl(String baseUrl) {
        UrlServiceImpl service = new UrlServiceImpl(
                urlRepository,
                shortCodeGenerator,
                null,
                null,
                null,
                null
        );
        ReflectionTestUtils.setField(service, "baseUrl", baseUrl);
        return service;
    }

    @Test
    void createShortUrl_buildsShortUrlFromConfiguredBaseUrl() {
        UrlServiceImpl urlService = serviceWithBaseUrl("http://localhost:8080");

        when(shortCodeGenerator.generateShortCode()).thenReturn("abc123");
        when(urlRepository.existsByShortCode("abc123")).thenReturn(false);

        UrlEntity saved = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://example.com/some/long/path")
                .shortCode("abc123")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(saved);

        UrlResponse response = urlService.createShortUrl(CreateUrlRequest.builder()
                .originalUrl("https://example.com/some/long/path")
                .build());

        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/abc123");
    }

    @Test
    void createShortUrl_usesCustomBaseUrlWhenConfigured() {
        UrlServiceImpl urlService = serviceWithBaseUrl("https://short.example.com");

        when(shortCodeGenerator.generateShortCode()).thenReturn("xyz789");
        when(urlRepository.existsByShortCode("xyz789")).thenReturn(false);

        UrlEntity saved = UrlEntity.builder()
                .id(2L)
                .originalUrl("https://example.com/other")
                .shortCode("xyz789")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(saved);

        UrlResponse response = urlService.createShortUrl(CreateUrlRequest.builder()
                .originalUrl("https://example.com/other")
                .build());

        assertThat(response.getShortUrl()).isEqualTo("https://short.example.com/xyz789");
    }
}
