package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.CardRequest;
import com.vietphan.bank_service.DTO.response.CardResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.CardMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    @Override
    @Transactional
    public CardResponse createCard(CardRequest request) {
        if (request == null || request.getAccountId() == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        Card card = cardMapper.toEntity(request, account);

        if (card.getCardType() == null) {
            card.setCardType(CardType.DEBIT);
        }

        if (card.getExpiryDate() == null) {
            card.setExpiryDate(LocalDate.now().plusYears(3));
        }

        if (card.getStatus() == null) {
            card.setStatus(CardStatus.ACTIVE);
        }

        if (card.getCardHolderName() == null || card.getCardHolderName().isBlank()) {
            card.setCardHolderName(account.getCustomerName().toUpperCase());
        }

        if (card.getCardNumber() != null && !card.getCardNumber().isBlank()) {
            if (cardRepository.existsByCardNumber(card.getCardNumber())) {
                throw new AppException(Errors.CARD_ALREADY_EXISTS);
            }
        } else {
            card.setCardNumber(generateUniqueCardNumber());
        }

        Card savedCard = cardRepository.save(card);
        return cardMapper.toResponse(savedCard);
    }

    @Override
    public List<CardResponse> getCardsByAccountId(UUID accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        return cardRepository.findByAccount(account).stream()
                .map(cardMapper::toResponse)
                .toList();
    }

    @Override
    public CardResponse getCardById(UUID cardId) {
        return cardRepository.findById(cardId)
                .map(cardMapper::toResponse)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));
    }

    @Override
    @Transactional
    public CardResponse deleteCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (card.isHasPendingTransactions()) {
            throw new AppException(Errors.CARD_HAS_PENDING_TRANSACTIONS);
        }

        card.setStatus(CardStatus.INACTIVE);
        Card deletedCard = cardRepository.save(card);
        return cardMapper.toResponse(deletedCard);
    }

    private String generateUniqueCardNumber() {
        Random random = new Random();
        String cardNumber;
        do {
            long number = 100000000000L + (long)(random.nextDouble() * 900000000000L);
            cardNumber = "9704" + number;
        } while (cardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }
}
