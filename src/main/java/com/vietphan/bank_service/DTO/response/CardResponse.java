package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
    private UUID cardId;
    private UUID accountId;
    private CardType cardType;
    private LocalDate expiryDate;
    private CardStatus status;
    private String cardNumber;
    private String cardHolderName;
    private Instant createdAt;
    private Instant updatedAt;
}
