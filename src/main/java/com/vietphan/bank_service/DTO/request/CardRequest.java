package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRequest {
    private Long accountId;
    private CardType cardType;
    private LocalDate expiryDate;
    private CardStatus status;
    private String cardNumber;
    private String cardHolderName;
    private Boolean hasPendingTransactions;
}
