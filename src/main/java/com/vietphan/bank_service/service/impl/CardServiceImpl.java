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

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final com.vietphan.bank_service.repository.BalanceRepository balanceRepository;
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
        com.vietphan.bank_service.entity.Balance balance = com.vietphan.bank_service.entity.Balance.builder()
                .account(account)
                .card(savedCard)
                .availableBalance(0.0)
                .holdBalance(0.0)
                .build();
        balanceRepository.save(balance);

        CardResponse response = cardMapper.toResponse(savedCard);
        response.setAvailableBalance(0.0);
        response.setHoldBalance(0.0);
        return response;
    }

    @Override
    public List<CardResponse> getCardsByAccountId(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        return cardRepository.findByAccount(account).stream()
                .map(card -> populateBalance(cardMapper.toResponse(card), card))
                .toList();
    }

    @Override
    public CardResponse getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .map(card -> populateBalance(cardMapper.toResponse(card), card))
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));
    }

    @Override
    @Transactional
    public CardResponse deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (card.isHasPendingTransactions()) {
            throw new AppException(Errors.CARD_HAS_PENDING_TRANSACTIONS);
        }

        card.setStatus(CardStatus.INACTIVE);
        Card deletedCard = cardRepository.save(card);
        return populateBalance(cardMapper.toResponse(deletedCard), deletedCard);
    }

    private CardResponse populateBalance(CardResponse response, Card card) {
        balanceRepository.findByCard(card).ifPresent(b -> {
            response.setAvailableBalance(b.getAvailableBalance());
            response.setHoldBalance(b.getHoldBalance());
        });
        return response;
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
