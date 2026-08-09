package com.Project.URL_Shortner.Filters;

import com.Project.URL_Shortner.Service.ServiceImpl.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr();
        log.debug("Rate limit check for IP: {} on path: {}", ipAddress, request.getRequestURI());

        if (!rateLimitService.isAllowed(ipAddress)) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", ipAddress, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status":429,
                        "message":"Too many requests. Please try again later."
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}