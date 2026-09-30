package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.AdminUpdateTransactionStatusRequest;
import com.vietphan.bank_service.DTO.response.AdminTransactionResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.entity.Transaction;
import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.service.AdminTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTransactionServiceImpl implements AdminTransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final BalanceRepository balanceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public AdminTransactionResponse getAllTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> txPage = transactionRepository.findAll(pageable);

        List<AdminTransactionResponse> content = txPage.getContent().stream()
                .map(this::toItemResponse)
                .toList();

        return AdminTransactionResponse.builder()
                .content(content)
                .pageNumber(txPage.getNumber())
                .pageSize(txPage.getSize())
                .totalElements(txPage.getTotalElements())
                .totalPages(txPage.getTotalPages())
                .last(txPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(Errors.TRANSACTION_NOT_FOUND));

        return toItemResponse(transaction);
    }

    @Override
    @Transactional
    public AdminTransactionResponse updateTransactionStatus(Long id, AdminUpdateTransactionStatusRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new AppException(Errors.TRANSACTION_NOT_FOUND));

        TransactionStatus newStatus = (request != null && request.getStatus() != null)
                ? request.getStatus()
                : TransactionStatus.SUCCESS;

        // If transitioning from PENDING to SUCCESS for a TRANSFER, settle hold and credit balances
        if (transaction.getStatus() == TransactionStatus.PENDING && newStatus == TransactionStatus.SUCCESS) {
            if (transaction.getTransactionType() == TransactionType.TRANSFER) {
                // Deduct holdBalance from sender
                Card senderCard = cardRepository.findByCardNumber(transaction.getFromCardNumber()).orElse(null);
                if (senderCard != null && senderCard.getAccount() != null) {
                    Account senderAcc = senderCard.getAccount();
                    balanceRepository.findByAccount(senderAcc).ifPresent(b -> {
                        b.setHoldBalance(Math.max(0, b.getHoldBalance() - transaction.getAmount()));
                        balanceRepository.save(b);
                        evictBalanceCache(senderAcc.getAccountId());
                    });
                }

                // Add availableBalance to recipient
                Card recipientCard = cardRepository.findByCardNumber(transaction.getToCardNumber()).orElse(null);
                if (recipientCard != null && recipientCard.getAccount() != null) {
                    Account recipientAcc = recipientCard.getAccount();
                    balanceRepository.findByAccount(recipientAcc).ifPresent(b -> {
                        b.setAvailableBalance(b.getAvailableBalance() + transaction.getAmount());
                        balanceRepository.save(b);
                        evictBalanceCache(recipientAcc.getAccountId());
                    });
                }
            }
        }

        transaction.setStatus(newStatus);
        Transaction saved = transactionRepository.save(transaction);
        log.info("Admin updated transaction {} status to {}", id, newStatus);

        return toItemResponse(saved);
    }

    private AdminTransactionResponse toItemResponse(Transaction tx) {
        String fromUserName = null;
        String toUserName = null;

        if (tx.getFromCardNumber() != null && !tx.getFromCardNumber().equals("0")) {
            fromUserName = cardRepository.findByCardNumber(tx.getFromCardNumber())
                    .map(c -> c.getCardHolderName() != null ? c.getCardHolderName() : (c.getAccount() != null ? c.getAccount().getCustomerName() : null))
                    .orElse(null);
        }

        if (tx.getToCardNumber() != null && !tx.getToCardNumber().equals("0")) {
            toUserName = cardRepository.findByCardNumber(tx.getToCardNumber())
                    .map(c -> c.getCardHolderName() != null ? c.getCardHolderName() : (c.getAccount() != null ? c.getAccount().getCustomerName() : null))
                    .orElse(null);
        }

        return AdminTransactionResponse.builder()
                .transactionId(tx.getTransactionId())
                .fromCardNumber(tx.getFromCardNumber())
                .toCardNumber(tx.getToCardNumber())
                .amount(tx.getAmount())
                .transactionType(tx.getTransactionType())
                .status(tx.getStatus())
                .createdAt(tx.getCreatedAt())
                .fromUserName(fromUserName)
                .toUserName(toUserName)
                .actions(List.of("VIEW", "UPDATE_STATUS"))
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
}
