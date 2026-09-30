package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.PaymentRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.PaymentResponse;
import com.vietphan.bank_service.client.PaymentClient;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/api/v1/payment", "/api/v1/payments"})
@RequiredArgsConstructor
public class PaymentProxyController {

    private final PaymentClient paymentClient;

    @PostMapping
    public BaseResponse<PaymentResponse> forwardPayment(@RequestBody PaymentRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        log.info("Processing proxy payment for accountId: {}, amount: {}", accountId, request.getAmount());

        request.setAccountId(accountId);
        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            request.setCurrency("VND");
        }

        return paymentClient.processPayment(request);
    }

    @GetMapping("/{paymentId}")
    public BaseResponse<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        log.info("Getting payment details for paymentId: {}", paymentId);
        return paymentClient.getPaymentById(paymentId);
    }

    @GetMapping("/my-payments")
    public BaseResponse<List<PaymentResponse>> getMyPayments() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        log.info("Getting payment history for accountId: {}", accountId);
        return paymentClient.getPaymentsByAccountId(accountId);
    }
}
