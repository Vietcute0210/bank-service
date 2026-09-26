package com.vietphan.bank_service.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferConfirmRequest {
    private Long transactionId;
    private String otpCode;
}
