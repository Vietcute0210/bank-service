package com.vietphan.bank_service.DTO.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceRequest {
    private Long accountId;
    private double availableBalance;
    private double holdBalance;
}
