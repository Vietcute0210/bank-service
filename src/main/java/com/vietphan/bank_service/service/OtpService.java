package com.vietphan.bank_service.service;

import com.vietphan.bank_service.entity.User;

public interface OtpService {
    String generateAndSendOtp(User user);
    void verifyOtp(User user, String otpCode);
}
