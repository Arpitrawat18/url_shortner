package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Dto.request.LoginRequest;
import com.Project.URL_Shortner.Dto.request.RegisterRequest;
import com.Project.URL_Shortner.Dto.response.AuthResponse;
import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Exception.EmailAlreadyExistsException;
import com.Project.URL_Shortner.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_duplicateEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("taken@example.com")
                .password("secret123")
                .build();

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByEmail("taken@example.com");
    }

    @Test
    void register_newEmail_returnsToken() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");

        UserEntity saved = UserEntity.builder()
                .id(1L)
                .name("Test User")
                .email("new@example.com")
                .password("encoded")
                .build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("new@example.com")
                .password("secret123")
                .build();

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_validCredentials_returnsToken() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .password("encoded")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("wrong-then-right")
                .build();

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_invalidCredentials_propagatesBadCredentialsException() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("wrong-password")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
