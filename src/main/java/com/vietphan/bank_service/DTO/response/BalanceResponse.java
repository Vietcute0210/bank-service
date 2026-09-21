package com.vietphan.bank_service.DTO.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {
    private UUID balanceId;
    private UUID accountId;
    private double availableBalance;
    private double holdBalance;
    private Instant createdAt;
    private Instant updatedAt;
}
