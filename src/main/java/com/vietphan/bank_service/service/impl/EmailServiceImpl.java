package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:your-email@gmail.com}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Mã OTP xác thực chuyển tiền - Bank Service");
            message.setText("Xin chào,\n\nMã OTP xác nhận chuyển khoản của bạn là: " 
                    + otpCode 
                    + "\n\nMã có hiệu lực trong 5 phút. Vui lòng tuyệt đối không chia sẻ mã này cho bất kỳ ai để bảo vệ tài khoản.\n\nTrân trọng,\nBank Service Team");

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.warn("Could not send email to {}: {}. Note: please configure valid SMTP credentials in application.properties.", toEmail, e.getMessage());
        }
    }
}
