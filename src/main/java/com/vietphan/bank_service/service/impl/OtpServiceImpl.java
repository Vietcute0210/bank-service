package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.message.OtpMessage;
import com.vietphan.bank_service.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JmsTemplate jmsTemplate;

    @Value("${activemq.queue.otp-requested:otp.requested}")
    private String otpQueue;

    @Value("${app.otp.ttl-seconds:180}")
    private long otpTtlSeconds;

    private static final String OTP_PREFIX = "otp:transaction:";

    @Override
    public String generateAndSaveOtp(Long transactionId, String email, String purpose) {
        String otpCode = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        String cacheKey = OTP_PREFIX + transactionId;

        // 1. Lưu OTP vào Redis với TTL (mặc định 3 phút)
        try {
            redisTemplate.opsForValue().set(cacheKey, otpCode, Duration.ofSeconds(otpTtlSeconds));
            log.info("Saved OTP for transactionId {} into Redis (TTL: {}s)", transactionId, otpTtlSeconds);
        } catch (Exception e) {
            log.warn("Failed to save OTP to Redis: {}", e.getMessage());
        }

        // 2. Bắn Message qua ActiveMQ cho notification-service (Cách 2)
        try {
            OtpMessage message = OtpMessage.builder()
                    .email(email)
                    .otpCode(otpCode)
                    .purpose(purpose != null ? purpose : "Xác thực giao dịch chuyển tiền")
                    .build();
            jmsTemplate.convertAndSend(otpQueue, message);
            log.info("Sent OtpMessage to ActiveMQ queue [{}] for email {}", otpQueue, email);
        } catch (Exception e) {
            log.error("Failed to send OtpMessage to ActiveMQ: {}", e.getMessage(), e);
        }

        return otpCode;
    }

    @Override
    public boolean validateOtp(Long transactionId, String inputOtp) {
        if (transactionId == null || inputOtp == null || inputOtp.isBlank()) {
            return false;
        }

        String cacheKey = OTP_PREFIX + transactionId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null && cached.toString().trim().equals(inputOtp.trim())) {
                redisTemplate.delete(cacheKey);
                log.info("OTP validated successfully for transactionId {}", transactionId);
                return true;
            }
        } catch (Exception e) {
            log.error("Error reading OTP from Redis: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public String getOtp(Long transactionId) {
        if (transactionId == null) return null;
        try {
            Object cached = redisTemplate.opsForValue().get(OTP_PREFIX + transactionId);
            return cached != null ? cached.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
