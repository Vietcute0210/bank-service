package com.vietphan.bank_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferInitiateResponse {
    private Long transactionId;
    private String fromCardNumber;
    private String toCardNumber;
    private double amount;
    private String message;
}
