package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.request.CardRequest;
import com.vietphan.bank_service.DTO.response.CardResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card toEntity(CardRequest request, Account account) {
        if (request == null) {
            return null;
        }

        return Card.builder()
                .account(account)
                .cardType(request.getCardType())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .build();
    }

    public CardResponse toResponse(Card card) {
        if (card == null) {
            return null;
        }

        return CardResponse.builder()
                .cardId(card.getCardId())
                .accountId(card.getAccount() != null ? card.getAccount().getAccountId() : null)
                .cardType(card.getCardType())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .cardNumber(card.getCardNumber())
                .cardHolderName(card.getCardHolderName())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    public void updateCard(Card card, CardRequest request) {
        if (card == null || request == null) {
            return;
        }

        if (request.getCardType() != null) {
            card.setCardType(request.getCardType());
        }
        if (request.getExpiryDate() != null) {
            card.setExpiryDate(request.getExpiryDate());
        }
        if (request.getStatus() != null) {
            card.setStatus(request.getStatus());
        }
        if (request.getCardNumber() != null) {
            card.setCardNumber(request.getCardNumber());
        }
        if (request.getCardHolderName() != null) {
            card.setCardHolderName(request.getCardHolderName());
        }
    }
}
