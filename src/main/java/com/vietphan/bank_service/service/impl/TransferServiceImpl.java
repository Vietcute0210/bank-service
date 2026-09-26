package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.DTO.response.CardSearchResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.entity.Transaction;
import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.BalanceMapper;
import com.vietphan.bank_service.mapper.TransactionMapper;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.repository.UserRepository;
import com.vietphan.bank_service.service.OtpService;
import com.vietphan.bank_service.service.TransferService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final TransactionMapper transactionMapper;
    private final BalanceMapper balanceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.ttl-minutes:10}")
    private long cacheTtlMinutes;

    @Override
    public CardSearchResponse searchCard(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new AppException(Errors.RECEIVER_CARD_NOT_FOUND);
        }

        Card card = cardRepository.findByCardNumber(cardNumber.trim())
                .orElseThrow(() -> new AppException(Errors.RECEIVER_CARD_NOT_FOUND));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new AppException(Errors.CARD_INACTIVE);
        }

        return CardSearchResponse.builder()
                .cardNumber(card.getCardNumber())
                .cardHolderName(card.getCardHolderName())
                .build();
    }

    @Override
    @Transactional
    public TransferInitiateResponse initiateTransfer(Long accountId, TransferInitiateRequest request) {
        if (request == null || request.getAmount() <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        String fromCardNumber = request.getFromCardNumber() != null ? request.getFromCardNumber().trim() : "";
        String toCardNumber = request.getToCardNumber() != null ? request.getToCardNumber().trim() : "";

        if (fromCardNumber.isBlank() || toCardNumber.isBlank()) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (fromCardNumber.equals(toCardNumber)) {
            throw new AppException(Errors.CANNOT_TRANSFER_TO_SAME_CARD);
        }

        Card fromCard = cardRepository.findByCardNumber(fromCardNumber)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (fromCard.getAccount() == null || !fromCard.getAccount().getAccountId().equals(accountId)) {
            throw new AppException(Errors.CARD_NOT_BELONG_TO_USER);
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new AppException(Errors.CARD_INACTIVE);
        }

        Card toCard = cardRepository.findByCardNumber(toCardNumber)
                .orElseThrow(() -> new AppException(Errors.RECEIVER_CARD_NOT_FOUND));

        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new AppException(Errors.CARD_INACTIVE);
        }

        Balance fromBalance = balanceRepository.findByAccount(fromCard.getAccount())
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (fromBalance.getAvailableBalance() < request.getAmount()) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        // Kiểm tra hạn mức chuyển tiền ngày theo User Level (nếu có cấu hình)
        User currentUser = userRepository.findByUsername(SecurityUtils.getCurrentUserDetails().getUsername())
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        if (currentUser.getLevel() != null && currentUser.getLevel().getDailyTransferLimit() > 0) {
            var startOfDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();
            List<String> userCardNumbers = cardRepository.findByAccount(fromCard.getAccount()).stream()
                    .map(Card::getCardNumber)
                    .toList();

            double transferredToday = transactionRepository.sumDailyTransfers(userCardNumbers, startOfDay);
            if (transferredToday + request.getAmount() > currentUser.getLevel().getDailyTransferLimit()) {
                throw new AppException(Errors.TRANSFER_LIMIT_EXCEEDED);
            }
        }

        // Tạo giao dịch PENDING
        Transaction transaction = Transaction.builder()
                .fromCardNumber(fromCard.getCardNumber())
                .toCardNumber(toCard.getCardNumber())
                .amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        // Sinh và gửi mã OTP qua Email
        otpService.generateAndSendOtp(currentUser);

        return TransferInitiateResponse.builder()
                .transactionId(savedTx.getTransactionId())
                .fromCardNumber(savedTx.getFromCardNumber())
                .toCardNumber(savedTx.getToCardNumber())
                .amount(savedTx.getAmount())
                .message("Mã OTP xác thực đã được gửi tới email của bạn. Vui lòng xác nhận giao dịch trong vòng 5 phút.")
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse confirmTransfer(Long accountId, TransferConfirmRequest request) {
        if (request == null || request.getTransactionId() == null || request.getOtpCode() == null) {
            throw new AppException(Errors.OTP_INVALID);
        }

        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(Errors.TRANSACTION_NOT_FOUND));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new AppException(Errors.TRANSACTION_ALREADY_PROCESSED);
        }

        Card fromCard = cardRepository.findByCardNumber(transaction.getFromCardNumber())
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (fromCard.getAccount() == null || !fromCard.getAccount().getAccountId().equals(accountId)) {
            throw new AppException(Errors.CARD_NOT_BELONG_TO_USER);
        }

        Card toCard = cardRepository.findByCardNumber(transaction.getToCardNumber())
                .orElseThrow(() -> new AppException(Errors.RECEIVER_CARD_NOT_FOUND));

        User currentUser = userRepository.findByUsername(SecurityUtils.getCurrentUserDetails().getUsername())
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        // Xác thực mã OTP
        otpService.verifyOtp(currentUser, request.getOtpCode());

        // Kiểm tra lại số dư tài khoản nguồn
        Balance fromBalance = balanceRepository.findByAccount(fromCard.getAccount())
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (fromBalance.getAvailableBalance() < transaction.getAmount()) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        // Trừ tiền tài khoản gửi
        fromBalance.setAvailableBalance(fromBalance.getAvailableBalance() - transaction.getAmount());
        Balance savedFromBalance = balanceRepository.save(fromBalance);

        // Cộng tiền tài khoản nhận
        Balance toBalance = balanceRepository.findByAccount(toCard.getAccount())
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));
        toBalance.setAvailableBalance(toBalance.getAvailableBalance() + transaction.getAmount());
        Balance savedToBalance = balanceRepository.save(toBalance);

        // Cập nhật lại Redis Cache cho cả hai bên
        updateBalanceCache(fromCard.getAccount().getAccountId(), savedFromBalance);
        updateBalanceCache(toCard.getAccount().getAccountId(), savedToBalance);

        // Cập nhật trạng thái giao dịch
        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction updatedTx = transactionRepository.save(transaction);

        return transactionMapper.toResponse(updatedTx);
    }

    private void updateBalanceCache(Long accountId, Balance balance) {
        String cacheKey = RedisConstants.BALANCE_CACHE_PREFIX + accountId;
        try {
            BalanceResponse response = balanceMapper.toResponse(balance);
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(cacheTtlMinutes));
        } catch (Exception e) {
            log.warn("Failed to update balance cache for accountId {}: {}", accountId, e.getMessage());
        }
    }
}
