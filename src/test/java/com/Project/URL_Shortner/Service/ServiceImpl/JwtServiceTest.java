package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "test-only-jwt-secret-key-do-not-use-in-production-0123456789";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
    }

    @Test
    void generateToken_createsValidTokenForUser() {
        UserEntity user = UserEntity.builder()
                .id(7L)
                .name("JWT Test User")
                .email("jwt@example.com")
                .build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.getUserIdFromToken(token)).isEqualTo(7L);
    }

    @Test
    void isTokenValid_returnsFalseForTamperedToken() {
        UserEntity user = UserEntity.builder()
                .id(7L)
                .name("JWT Test User")
                .email("jwt@example.com")
                .build();

        String token = jwtService.generateToken(user);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}
