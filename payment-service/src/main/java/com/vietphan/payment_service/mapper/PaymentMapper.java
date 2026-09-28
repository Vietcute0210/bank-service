package com.vietphan.payment_service.mapper;

import com.vietphan.payment_service.DTO.message.PaymentMessage;
import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.PaymentResponse;
import com.vietphan.payment_service.entity.Payment;
import com.vietphan.payment_service.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }

        return Payment.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .description(request.getDescription())
                .status(PaymentStatus.PENDING)
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .accountId(payment.getAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public PaymentMessage toMessage(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentMessage.builder()
                .paymentId(payment.getPaymentId())
                .accountId(payment.getAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus() != null ? payment.getStatus().name() : PaymentStatus.SUCCESS.name())
                .timestamp(payment.getCreatedAt() != null ? payment.getCreatedAt() : Instant.now())
                .build();
    }
}
