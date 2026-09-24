package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
    private Long cardId;
    private Long accountId;
    private CardType cardType;
    private LocalDate expiryDate;
    private CardStatus status;
    private String cardNumber;
    private String cardHolderName;
    private boolean hasPendingTransactions;
    private Instant createdAt;
    private Instant updatedAt;
}
