package com.vietphan.payment_service.service;

import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    List<PaymentResponse> getPaymentsByAccountId(Long accountId);
}
