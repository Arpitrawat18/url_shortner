package com.Project.URL_Shortner.Service;

import com.Project.URL_Shortner.Dto.request.CreateUrlRequest;
import com.Project.URL_Shortner.Dto.response.AnalyticsResponse;
import com.Project.URL_Shortner.Dto.response.UrlResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UrlService {
    UrlResponse createShortUrl(CreateUrlRequest request);

    String getOriginalUrl(String shortCode, HttpServletRequest request);

    AnalyticsResponse getAnalytics(String shortCode);
    List<UrlResponse> getMyUrls();
    void deleteUrl(String shortCode);
}
