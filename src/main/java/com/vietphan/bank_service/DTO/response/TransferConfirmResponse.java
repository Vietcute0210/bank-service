package com.vietphan.bank_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferConfirmResponse {
    private Long transactionId;
    private Long paymentId;
    private String fromCardNumber;
    private String toCardNumber;
    private double amount;
    private String status;
    private String message;
    private Instant completedAt;
}
