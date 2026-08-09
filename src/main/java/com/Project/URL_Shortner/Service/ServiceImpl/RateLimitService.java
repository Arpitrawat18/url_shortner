package com.Project.URL_Shortner.Service.ServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final long LIMIT = 100;
    private static final long WINDOW = 60;

    public boolean isAllowed(String ipAddress) {
        log.debug("Checking rate limit for IP: {}", ipAddress);
        
        String key = "rate_limit:" + ipAddress;

        try {
            Long count = redisTemplate.opsForValue().increment(key);

            if (count == null) {
                log.error("Error retrieving rate limit count for IP: {}", ipAddress);
                return false;
            }

            if (count == 1) {
                redisTemplate.expire(key, WINDOW, TimeUnit.SECONDS);
                log.debug("New rate limit window started for IP: {}", ipAddress);
            }

            if (count > LIMIT) {
                log.warn("Rate limit exceeded for IP: {} ({}/ {} requests)", ipAddress, count, LIMIT);
                return false;
            }
            
            log.debug("Rate limit check passed for IP: {} ({}/{} requests)", ipAddress, count, LIMIT);
            return true;
        } catch (Exception e) {
            log.warn("Redis unavailable during rate limit check for IP: {} - allowing request: {}", ipAddress, e.getMessage());
            return true;
        }
    }
}