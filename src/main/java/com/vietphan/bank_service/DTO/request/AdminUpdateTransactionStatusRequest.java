package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateTransactionStatusRequest {
    private TransactionStatus status;
}
