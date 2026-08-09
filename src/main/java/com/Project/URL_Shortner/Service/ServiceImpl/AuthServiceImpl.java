package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Dto.request.LoginRequest;
import com.Project.URL_Shortner.Dto.request.RegisterRequest;
import com.Project.URL_Shortner.Dto.response.AuthResponse;
import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Exception.EmailAlreadyExistsException;
import com.Project.URL_Shortner.Repository.UserRepository;
import com.Project.URL_Shortner.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Starting registration process for email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.debug("User saved successfully with ID: {}", savedUser.getId());

        String token = jwtService.generateToken(user);
        log.debug("JWT token generated for user: {}", request.getEmail());

        log.info("Registration completed successfully for email: {}", request.getEmail());
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Starting login process for email: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            log.debug("User authenticated successfully: {}", request.getEmail());

            UserEntity user = (UserEntity) authentication.getPrincipal();

            String token = jwtService.generateToken(user);
            log.debug("JWT token generated for user: {}", request.getEmail());
            
            log.info("Login completed successfully for email: {}", request.getEmail());
            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (Exception e) {
            log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}
