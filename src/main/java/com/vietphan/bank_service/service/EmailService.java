package com.vietphan.bank_service.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
}
