package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateCardRequest {
    private Long userId;
    private Long accountId;
    private CardType cardType;
    private String cardNumber;
    private String cardHolderName;
    private LocalDate expiryDate;
}
