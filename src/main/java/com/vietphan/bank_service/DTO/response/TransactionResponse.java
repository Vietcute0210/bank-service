package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long transactionId;

    private String fromCardNumber;

    private String toCardNumber;

    private double amount;

    private TransactionType transactionType;

    private TransactionStatus status;

    private Instant createdAt;
}