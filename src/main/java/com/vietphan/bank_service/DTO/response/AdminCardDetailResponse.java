package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCardDetailResponse {
    // Card info
    private Long cardId;
    private String cardNumber;
    private CardType cardType;
    private LocalDate expiryDate;
    private CardStatus status;
    private Long userId;
    private String userName;
    private String userEmail;
    private String cardHolderName;

    // Balance info
    private Long balanceId;
    private Double availableBalance;
    private Double holdBalance;
    private Instant lastUpdated;
    private String currency;

    // Nested structures
    private CardInfo cardInfo;
    private BalanceInfo balanceInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardInfo {
        private Long cardId;
        private String cardNumber;
        private CardType cardType;
        private LocalDate expiryDate;
        private CardStatus status;
        private Long userId;
        private String userName;
        private String userEmail;
        private String cardHolderName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalanceInfo {
        private Long balanceId;
        private Double availableBalance;
        private Double holdBalance;
        private Instant lastUpdated;
        private String currency;
    }
}
