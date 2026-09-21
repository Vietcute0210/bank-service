package com.vietphan.bank_service.DTO.request;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceRequest {
    private UUID accountId;
    private double availableBalance;
    private double holdBalance;
}
