package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCardResponse {
    private Long cardId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String cardNumber;
    private CardType cardType;
    private CardStatus status;
    private String cardHolderName;
    private LocalDate expiryDate;
    private List<String> actions;
    private Instant createdAt;
    private Instant updatedAt;
}
