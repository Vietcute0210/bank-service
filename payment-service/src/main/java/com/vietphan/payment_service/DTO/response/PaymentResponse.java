package com.vietphan.payment_service.DTO.response;

import com.vietphan.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    private Long accountId;
    private double amount;
    private String currency;
    private PaymentStatus status;
    private String description;
    private Instant createdAt;
}
