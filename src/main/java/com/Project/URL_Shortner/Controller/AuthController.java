package com.Project.URL_Shortner.Controller;

import com.Project.URL_Shortner.Dto.request.LoginRequest;
import com.Project.URL_Shortner.Dto.request.RegisterRequest;
import com.Project.URL_Shortner.Dto.response.AuthResponse;
import com.Project.URL_Shortner.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully with email: {}", request.getEmail());
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully with email: {}", request.getEmail());
        return response;
    }

}