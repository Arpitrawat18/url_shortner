package com.Project.URL_Shortner.Scheduler;

import com.Project.URL_Shortner.Repository.AnalyticsRepository;
import com.Project.URL_Shortner.Repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupScheduler {

    private final UrlRepository urlRepository;
    private final AnalyticsRepository analyticsRepository;

    @Scheduled(cron = "${cleanup.cron}")
    @Transactional
    public void deleteExpiredUrls() {

        log.info("Starting expired URL cleanup...");

        int analyticsDeleted = analyticsRepository.deleteAnalyticsForExpiredUrls();
        int deleted = urlRepository.deleteExpiredUrls();

        log.info("Expired URL cleanup completed. Deleted {} expired URLs and {} analytics rows.", deleted, analyticsDeleted);
    }
}