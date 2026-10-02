package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.entity.Transaction;
import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.BalanceMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceMapper balanceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.ttl-minutes:10}")
    private long cacheTtlMinutes;

    @Override
    public BalanceResponse getBalance(Long accountId) {
        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        String keyCache = RedisConstants.BALANCE_CACHE_PREFIX + accountId;
        try {
            Object cache = redisTemplate.opsForValue().get(keyCache);
            if (cache instanceof BalanceResponse cachedBalance) {
                return cachedBalance;
            }
        } catch (Exception e) {
            System.err.println("Redis error on getBalance: " + e.getMessage());
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        List<Balance> balances = balanceRepository.findByAccount(account);
        double totalAvailable = balances.stream().mapToDouble(Balance::getAvailableBalance).sum();
        double totalHold = balances.stream().mapToDouble(Balance::getHoldBalance).sum();

        BalanceResponse response = BalanceResponse.builder()
                .accountId(accountId)
                .availableBalance(totalAvailable)
                .holdBalance(totalHold)
                .build();

        try {
            redisTemplate.opsForValue().set(keyCache, response, Duration.ofMinutes(cacheTtlMinutes));
        } catch (Exception e) {
            System.err.println("Redis error on setBalance: " + e.getMessage());
        }

        return response;
    }

    @Override
    @Transactional
    public BalanceResponse addBalance(Long accountId, Long cardId, double money) {
        if (money <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (!card.getAccount().getAccountId().equals(accountId)) {
            throw new AppException(Errors.FORBIDDEN);
        }

        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        balance.setAvailableBalance(balance.getAvailableBalance() + money);
        Balance updatedBalance = balanceRepository.save(balance);

        String cardNumber = card.getCardNumber();

        // Ghi log giao dịch DEPOSIT
        Transaction transaction = Transaction.builder()
                .fromCardNumber("0")
                .toCardNumber(cardNumber)
                .amount(money)
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);

        // Xóa cache cũ để getBalance() tính lại tổng chính xác từ tất cả thẻ
        String keyCache = RedisConstants.BALANCE_CACHE_PREFIX + accountId;
        try {
            redisTemplate.delete(keyCache);
        } catch (Exception e) {
            System.err.println("Redis error on delete addBalance cache: " + e.getMessage());
        }

        return getBalance(accountId);
    }

    @Override
    @Transactional
    public BalanceResponse subtractBalance(Long accountId, Long cardId, double money) {
        if (money <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (!card.getAccount().getAccountId().equals(accountId)) {
            throw new AppException(Errors.FORBIDDEN);
        }

        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (balance.getAvailableBalance() < money) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        balance.setAvailableBalance(balance.getAvailableBalance() - money);
        Balance updatedBalance = balanceRepository.save(balance);

        String cardNumber = card.getCardNumber();

        // Ghi log giao dịch WITHDRAWAL
        Transaction transaction = Transaction.builder()
                .fromCardNumber(cardNumber)
                .toCardNumber("0")
                .amount(money)
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);

        // Xóa cache cũ để getBalance() tính lại tổng chính xác từ tất cả thẻ
        String keyCache = RedisConstants.BALANCE_CACHE_PREFIX + accountId;
        try {
            redisTemplate.delete(keyCache);
        } catch (Exception e) {
            System.err.println("Redis error on delete subtractBalance cache: " + e.getMessage());
        }

        return getBalance(accountId);
    }
}
