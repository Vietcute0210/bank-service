package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.entity.Otp;
import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.repository.OtpRepository;
import com.vietphan.bank_service.service.EmailService;
import com.vietphan.bank_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String generateAndSendOtp(User user) {
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));
        Instant expiryTime = Instant.now().plus(5, ChronoUnit.MINUTES);

        Otp otp = Otp.builder()
                .user(user)
                .otpCode(otpCode)
                .expiryTime(expiryTime)
                .verified(false)
                .build();
        otpRepository.save(otp);

        String recipientEmail = (user.getAccount() != null && user.getAccount().getEmail() != null)
                ? user.getAccount().getEmail()
                : "user@example.com";

        emailService.sendOtpEmail(recipientEmail, otpCode);
        return otpCode;
    }

    @Override
    @Transactional
    public void verifyOtp(User user, String otpCode) {
        if (otpCode == null || otpCode.trim().length() != 6) {
            throw new AppException(Errors.OTP_INVALID);
        }

        Otp otp = otpRepository.findTopByUserAndVerifiedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new AppException(Errors.OTP_INVALID));

        if (Instant.now().isAfter(otp.getExpiryTime())) {
            throw new AppException(Errors.OTP_EXPIRED);
        }

        if (!otp.getOtpCode().equals(otpCode.trim())) {
            throw new AppException(Errors.OTP_INVALID);
        }

        otp.setVerified(true);
        otpRepository.save(otp);
    }
}
