package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.AdminCreateCardRequest;
import com.vietphan.bank_service.DTO.request.AdminDepositWithdrawRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateCardRequest;
import com.vietphan.bank_service.DTO.response.AdminCardDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminCardResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.*;
import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.repository.*;
import com.vietphan.bank_service.service.AdminCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCardServiceImpl implements AdminCardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AdminCardResponse> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream().map(this::toAdminCardResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminCardDetailResponse getCardDetail(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        Account account = card.getAccount();
        User user = (account != null) ? userRepository.findByAccount(account).orElse(null) : null;
        Balance balance = balanceRepository.findByCard(card).orElse(null);

        return buildCardDetailResponse(card, account, user, balance);
    }

    @Override
    @Transactional
    public AdminCardResponse createCard(AdminCreateCardRequest request) {
        Account account = null;

        if (request.getAccountId() != null) {
            account = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));
        } else if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(Errors.USER_NOT_FOUND));
            account = user.getAccount();
            if (account == null) {
                throw new AppException(Errors.ACCOUNT_NOT_FOUND);
            }
        } else {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        // Check card limit for user level if assigned
        Optional<User> userOpt = userRepository.findByAccount(account);
        if (userOpt.isPresent() && userOpt.get().getLevel() != null) {
            UserLevel level = userOpt.get().getLevel();
            int currentCardCount = cardRepository.findByAccount(account).size();
            if (level.getCardLimit() > 0 && currentCardCount >= level.getCardLimit()) {
                throw new AppException(Errors.CARD_LIMIT_EXCEEDED);
            }
        }

        String cardNumber = request.getCardNumber();
        if (cardNumber != null && !cardNumber.isBlank()) {
            if (cardRepository.existsByCardNumber(cardNumber)) {
                throw new AppException(Errors.CARD_ALREADY_EXISTS);
            }
        } else {
            cardNumber = generateUniqueCardNumber();
        }

        String cardHolderName = (request.getCardHolderName() != null && !request.getCardHolderName().isBlank())
                ? request.getCardHolderName().toUpperCase()
                : (account.getCustomerName() != null ? account.getCustomerName().toUpperCase() : "CUSTOMER");

        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .cardHolderName(cardHolderName)
                .cardType(request.getCardType() != null ? request.getCardType() : CardType.DEBIT)
                .expiryDate(request.getExpiryDate() != null ? request.getExpiryDate() : LocalDate.now().plusYears(3))
                .status(CardStatus.ACTIVE)
                .hasPendingTransactions(false)
                .build();

        Card savedCard = cardRepository.save(card);
        return toAdminCardResponse(savedCard);
    }

    @Override
    @Transactional
    public AdminCardResponse updateCard(Long cardId, AdminUpdateCardRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (request.getStatus() != null) {
            card.setStatus(request.getStatus());
        }

        if (request.getCardType() != null) {
            card.setCardType(request.getCardType());
        }

        Card updatedCard = cardRepository.save(card);
        return toAdminCardResponse(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (card.isHasPendingTransactions()) {
            throw new AppException(Errors.CARD_HAS_PENDING_TRANSACTIONS);
        }

        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public AdminCardDetailResponse depositToCard(Long cardId, AdminDepositWithdrawRequest request) {
        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        Account account = card.getAccount();
        if (account == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        balance.setAvailableBalance(balance.getAvailableBalance() + request.getAmount());
        Balance savedBalance = balanceRepository.save(balance);

        Transaction transaction = Transaction.builder()
                .fromCardNumber("0")
                .toCardNumber(card.getCardNumber())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);

        evictBalanceCache(account.getAccountId());

        User user = userRepository.findByAccount(account).orElse(null);
        return buildCardDetailResponse(card, account, user, savedBalance);
    }

    @Override
    @Transactional
    public AdminCardDetailResponse withdrawFromCard(Long cardId, AdminDepositWithdrawRequest request) {
        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (request.getAmount() <= 1000000) {
            throw new AppException(Errors.WITHDRAW_MIN_AMOUNT);
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        Account account = card.getAccount();
        if (account == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (balance.getAvailableBalance() < request.getAmount()) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        balance.setAvailableBalance(balance.getAvailableBalance() - request.getAmount());
        Balance savedBalance = balanceRepository.save(balance);

        Transaction transaction = Transaction.builder()
                .fromCardNumber(card.getCardNumber())
                .toCardNumber("0")
                .amount(request.getAmount())
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);

        evictBalanceCache(account.getAccountId());

        User user = userRepository.findByAccount(account).orElse(null);
        return buildCardDetailResponse(card, account, user, savedBalance);
    }

    private AdminCardResponse toAdminCardResponse(Card card) {
        Account account = card.getAccount();
        User user = (account != null) ? userRepository.findByAccount(account).orElse(null) : null;

        return AdminCardResponse.builder()
                .cardId(card.getCardId())
                .userId(user != null ? user.getUserId() : null)
                .userName(user != null ? user.getUsername() : (account != null ? account.getCustomerName() : null))
                .userEmail(account != null ? account.getEmail() : null)
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .cardHolderName(card.getCardHolderName())
                .expiryDate(card.getExpiryDate())
                .actions(List.of("VIEW", "EDIT", "DELETE", "DEPOSIT", "WITHDRAW"))
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    private AdminCardDetailResponse buildCardDetailResponse(Card card, Account account, User user, Balance balance) {
        Long userId = user != null ? user.getUserId() : null;
        String userName = user != null ? user.getUsername() : (account != null ? account.getCustomerName() : null);
        String userEmail = account != null ? account.getEmail() : null;

        Long balanceId = balance != null ? balance.getBalanceId() : null;
        Double availableBalance = balance != null ? balance.getAvailableBalance() : 0.0;
        Double holdBalance = balance != null ? balance.getHoldBalance() : 0.0;
        java.time.Instant lastUpdated = balance != null ? balance.getUpdatedAt() : null;
        String currency = account != null ? account.getCurrency() : "VND";

        AdminCardDetailResponse.CardInfo cardInfo = AdminCardDetailResponse.CardInfo.builder()
                .cardId(card.getCardId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .cardHolderName(card.getCardHolderName())
                .build();

        AdminCardDetailResponse.BalanceInfo balanceInfo = AdminCardDetailResponse.BalanceInfo.builder()
                .balanceId(balanceId)
                .availableBalance(availableBalance)
                .holdBalance(holdBalance)
                .lastUpdated(lastUpdated)
                .currency(currency)
                .build();

        return AdminCardDetailResponse.builder()
                .cardId(card.getCardId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .cardHolderName(card.getCardHolderName())
                .balanceId(balanceId)
                .availableBalance(availableBalance)
                .holdBalance(holdBalance)
                .lastUpdated(lastUpdated)
                .currency(currency)
                .cardInfo(cardInfo)
                .balanceInfo(balanceInfo)
                .build();
    }

    private void evictBalanceCache(Long accountId) {
        if (accountId == null) return;
        try {
            redisTemplate.delete(RedisConstants.BALANCE_CACHE_PREFIX + accountId);
        } catch (Exception e) {
            log.warn("Failed to delete cache for accountId {}: {}", accountId, e.getMessage());
        }
    }

    private String generateUniqueCardNumber() {
        Random random = new Random();
        String cardNumber;
        do {
            long number = 100000000000L + (long) (random.nextDouble() * 900000000000L);
            cardNumber = "9704" + number;
        } while (cardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }
}
