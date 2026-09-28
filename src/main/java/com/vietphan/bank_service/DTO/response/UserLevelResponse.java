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
public class UserLevelResponse {

    private Long levelId;
    private String levelName;
    private int cardLimit;
    private double dailyTransferLimit;
    private Instant createdAt;
    private Instant updatedAt;
}
