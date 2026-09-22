package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.LoginRequest;
import com.vietphan.bank_service.DTO.request.RefreshTokenRequest;
import com.vietphan.bank_service.DTO.request.RegisterRequest;
import com.vietphan.bank_service.DTO.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshTokenStr);
}