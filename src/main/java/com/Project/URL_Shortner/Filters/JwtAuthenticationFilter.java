package com.Project.URL_Shortner.Filters;

import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Repository.UserRepository;
import com.Project.URL_Shortner.Service.ServiceImpl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/actuator");
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.debug("JWT Filter processing request to: {}", request.getRequestURI());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Authorization header or invalid format found for path: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        log.debug("JWT token found, validating...");

        if (!jwtService.isTokenValid(jwt)) {
            log.warn("JWT token validation failed for path: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = jwtService.getUserIdFromToken(jwt);
            log.debug("User ID extracted from token: {}", userId);

            UserEntity user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                log.warn("User not found for ID: {} from JWT token", userId);
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Setting authentication for user: {}", user.getEmail());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set successfully for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error processing JWT token - {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}