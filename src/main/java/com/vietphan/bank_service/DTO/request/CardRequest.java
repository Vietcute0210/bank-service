package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRequest {
    private UUID accountId;
    private CardType cardType;
    private LocalDate expiryDate;
    private CardStatus status;
    private String cardNumber;
    private String cardHolderName;
    private Boolean hasPendingTransactions;
}
