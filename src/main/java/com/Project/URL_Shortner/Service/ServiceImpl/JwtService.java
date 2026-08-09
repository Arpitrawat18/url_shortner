package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(UserEntity user) {
        log.debug("Generating JWT token for user ID: {}, email: {}", user.getId(), user.getEmail());
        try {
            String token = Jwts.builder()
                    .subject(user.getId().toString())
                    .claim("name", user.getName())
                    .claim("email", user.getEmail())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
                    .signWith(getSecretKey())
                    .compact();
            log.debug("JWT token generated successfully for user: {}", user.getEmail());
            return token;
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {} - {}", user.getEmail(), e.getMessage());
            throw e;
        }
    }
    
    public Long getUserIdFromToken(String token) {
        log.debug("Extracting user ID from JWT token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.valueOf(claims.getSubject());
            log.debug("User ID extracted from token: {}", userId);
            return userId;
        } catch (JwtException e) {
            log.error("Error extracting user ID from token - {}", e.getMessage());
            throw e;
        }
    }
    
    public boolean isTokenValid(String token) {
        log.debug("Validating JWT token");
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            log.debug("JWT token is valid");
            return true;
        } catch (JwtException e) {
            log.debug("JWT token validation failed - {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Unexpected error validating JWT token - {}", e.getMessage());
            return false;
        }
    }

}
