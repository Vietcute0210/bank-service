package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.LoginRequest;
import com.vietphan.bank_service.DTO.request.RefreshTokenRequest;
import com.vietphan.bank_service.DTO.request.RegisterRequest;
import com.vietphan.bank_service.DTO.response.AuthResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public BaseResponse<AuthResponse> login(@RequestBody LoginRequest request){
        var result = authService.login(request);
        return BaseResponse.<AuthResponse>builder()
                .code(1000)
                .message("login successfully")
                .data(result)
                .build();
    }

    @PostMapping("/register")
    public BaseResponse<AuthResponse> register(@RequestBody RegisterRequest request){
        var result = authService.register(request);
        return BaseResponse.<AuthResponse>builder()
                .code(1000)
                .message("register successfully")
                .data(result)
                .build();
    }

    @PostMapping("/refresh")
    public BaseResponse<AuthResponse> refresh(@RequestBody RefreshTokenRequest request){
        var result = authService.refreshToken(request);
        return BaseResponse.<AuthResponse>builder()
                .code(1000)
                .message("refresh token successfully")
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return BaseResponse.<String>builder()
                .code(1000)
                .message("Logout successfully")
                .data("Logged out")
                .build();
    }
}
