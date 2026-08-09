package com.Project.URL_Shortner.Service;

import com.Project.URL_Shortner.Dto.request.LoginRequest;
import com.Project.URL_Shortner.Dto.request.RegisterRequest;
import com.Project.URL_Shortner.Dto.response.AuthResponse;
import jakarta.validation.Valid;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(@Valid RegisterRequest request);
}