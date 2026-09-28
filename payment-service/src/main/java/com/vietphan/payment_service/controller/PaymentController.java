package com.vietphan.payment_service.controller;

import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.BaseResponse;
import com.vietphan.payment_service.DTO.response.PaymentResponse;
import com.vietphan.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public BaseResponse<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return BaseResponse.<PaymentResponse>builder()
                .code(1000)
                .message("Payment processed successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{paymentId}")
    public BaseResponse<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return BaseResponse.<PaymentResponse>builder()
                .code(1000)
                .message("Get payment successfully")
                .data(response)
                .build();
    }

    @GetMapping("/account/{accountId}")
    public BaseResponse<List<PaymentResponse>> getPaymentsByAccountId(@PathVariable Long accountId) {
        List<PaymentResponse> response = paymentService.getPaymentsByAccountId(accountId);
        return BaseResponse.<List<PaymentResponse>>builder()
                .code(1000)
                .message("Get account payments successfully")
                .data(response)
                .build();
    }
}
