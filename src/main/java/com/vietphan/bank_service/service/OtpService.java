package com.vietphan.bank_service.service;

public interface OtpService {

    String generateAndSaveOtp(Long transactionId, String email, String purpose);

    boolean validateOtp(Long transactionId, String inputOtp);

    String getOtp(Long transactionId);
}
