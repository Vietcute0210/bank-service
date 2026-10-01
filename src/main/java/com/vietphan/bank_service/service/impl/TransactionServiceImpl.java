package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.PaymentRequest;
import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.PaymentResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferConfirmResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;
import com.vietphan.bank_service.client.PaymentClient;
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
import com.vietphan.bank_service.mapper.TransactionMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.service.OtpService;
import com.vietphan.bank_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final BalanceRepository balanceRepository;
    private final OtpService otpService;
    private final PaymentClient paymentClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<TransactionResponse> getMyTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        List<Card> cards = cardRepository.findByAccount(account);
        if (cards.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> cardNumbers = cards.stream()
                .map(Card::getCardNumber)
                .filter(number -> number != null && !number.isBlank())
                .toList();

        if (cardNumbers.isEmpty()) {
            return Collections.emptyList();
        }

        List<Transaction> transactions = transactionRepository.findByCardNumbers(cardNumbers);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByCard(Long accountId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));

        if (card.getAccount() == null || !card.getAccount().getAccountId().equals(accountId)) {
            throw new AppException(Errors.CARD_NOT_FOUND);
        }

        List<Transaction> transactions = transactionRepository.findByCardNumber(card.getCardNumber());
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public TransactionResponse getTransaction(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new AppException(Errors.TRANSACTION_NOT_FOUND));
    }

    @Override
    @Transactional
    public TransferInitiateResponse initiateTransfer(Long accountId, TransferInitiateRequest request) {
        if (request.getAmount() <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        Account senderAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        // Tìm thẻ nguồn (fromCard)
        String fromCardNumber = request.getFromCardNumber();
        Card senderCard = null;
        if (fromCardNumber == null || fromCardNumber.isBlank()) {
            List<Card> senderCards = cardRepository.findByAccount(senderAccount);
            senderCard = senderCards.stream()
                    .filter(c -> c.getStatus() == CardStatus.ACTIVE)
                    .findFirst()
                    .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));
            fromCardNumber = senderCard.getCardNumber();
        } else {
            senderCard = cardRepository.findByCardNumber(fromCardNumber)
                    .orElseThrow(() -> new AppException(Errors.CARD_NOT_FOUND));
        }

        Balance senderBalance = balanceRepository.findByCard(senderCard)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (senderBalance.getAvailableBalance() < request.getAmount()) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        // Kiểm tra thẻ đích (toCard)
        String toCardNumber = request.getToCardNumber();
        if (toCardNumber == null || toCardNumber.isBlank()) {
            throw new AppException(Errors.CARD_NOT_FOUND);
        }

        if (fromCardNumber.equals(toCardNumber)) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        // Tạm giữ số dư (Hold balance: available -> hold)
        senderBalance.setAvailableBalance(senderBalance.getAvailableBalance() - request.getAmount());
        senderBalance.setHoldBalance(senderBalance.getHoldBalance() + request.getAmount());
        balanceRepository.save(senderBalance);

        // Tạo Transaction trạng thái PENDING
        Transaction transaction = Transaction.builder()
                .fromCardNumber(fromCardNumber)
                .toCardNumber(toCardNumber)
                .amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .build();
        Transaction savedTx = transactionRepository.save(transaction);

        // Sinh mã OTP, lưu Redis, gửi message ActiveMQ
        String email = senderAccount.getEmail() != null ? senderAccount.getEmail() : "user" + accountId + "@example.com";
        String otpCode = otpService.generateAndSaveOtp(savedTx.getTransactionId(), email, "Xác thực giao dịch chuyển tiền");

        log.info("Transfer initiated: txId={}, fromCard={}, toCard={}, amount={}, otp={}",
                savedTx.getTransactionId(), fromCardNumber, toCardNumber, request.getAmount(), otpCode);

        return TransferInitiateResponse.builder()
                .transactionId(savedTx.getTransactionId())
                .fromCardNumber(fromCardNumber)
                .toCardNumber(toCardNumber)
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING.name())
                .otp(otpCode)
                .message("Khởi tạo chuyển tiền thành công. Mã OTP đã được gửi đến email của bạn!")
                .build();
    }

    @Override
    @Transactional
    public TransferConfirmResponse confirmTransfer(Long accountId, TransferConfirmRequest request) {
        if (request.getTransactionId() == null || request.getOtp() == null || request.getOtp().isBlank()) {
            throw new AppException(Errors.INVALID_OTP);
        }

        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(Errors.TRANSACTION_NOT_FOUND));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new AppException(Errors.TRANSACTION_ALREADY_PROCESSED);
        }

        // Xác thực mã OTP từ Redis
        boolean isOtpValid = otpService.validateOtp(request.getTransactionId(), request.getOtp());
        if (!isOtpValid) {
            log.warn("Invalid or expired OTP for transactionId {}", request.getTransactionId());
            throw new AppException(Errors.INVALID_OTP);
        }

        // Hoàn tất chuyển tiền:
        // 1. Trừ holdBalance của người gửi
        Card senderCard = cardRepository.findByCardNumber(transaction.getFromCardNumber())
                .orElse(null);
        if (senderCard != null) {
            balanceRepository.findByCard(senderCard).ifPresent(b -> {
                b.setHoldBalance(Math.max(0, b.getHoldBalance() - transaction.getAmount()));
                balanceRepository.save(b);
                if (senderCard.getAccount() != null) {
                    try {
                        redisTemplate.delete(RedisConstants.BALANCE_CACHE_PREFIX + senderCard.getAccount().getAccountId());
                    } catch (Exception ignored) {}
                }
            });
        }

        // 2. Cộng availableBalance cho người nhận (nếu có tài khoản trong hệ thống)
        Card recipientCard = cardRepository.findByCardNumber(transaction.getToCardNumber())
                .orElse(null);
        if (recipientCard != null) {
            balanceRepository.findByCard(recipientCard).ifPresent(b -> {
                b.setAvailableBalance(b.getAvailableBalance() + transaction.getAmount());
                balanceRepository.save(b);
                if (recipientCard.getAccount() != null) {
                    try {
                        redisTemplate.delete(RedisConstants.BALANCE_CACHE_PREFIX + recipientCard.getAccount().getAccountId());
                    } catch (Exception ignored) {}
                }
            });
        }

        // 3. Cập nhật Transaction sang SUCCESS
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
        log.info("Transaction {} completed successfully", transaction.getTransactionId());

        // 4. Gọi payment-service để ghi nhận payment và kích hoạt notification qua ActiveMQ
        Long paymentId = null;
        try {
            PaymentRequest payReq = PaymentRequest.builder()
                    .accountId(accountId)
                    .amount(transaction.getAmount())
                    .currency("VND")
                    .description("Chuyen khoan tu " + transaction.getFromCardNumber() + " den " + transaction.getToCardNumber())
                    .build();
            BaseResponse<PaymentResponse> payResp = paymentClient.processPayment(payReq);
            if (payResp != null && payResp.getData() != null) {
                paymentId = payResp.getData().getPaymentId();
                log.info("Payment registered in payment-service with paymentId: {}", paymentId);
            }
        } catch (Exception e) {
            log.warn("Warning: Could not forward to payment-service: {}", e.getMessage());
        }

        return TransferConfirmResponse.builder()
                .transactionId(transaction.getTransactionId())
                .paymentId(paymentId)
                .fromCardNumber(transaction.getFromCardNumber())
                .toCardNumber(transaction.getToCardNumber())
                .amount(transaction.getAmount())
                .status(TransactionStatus.SUCCESS.name())
                .message("Xác thực OTP thành công. Giao dịch chuyển tiền đã hoàn tất!")
                .completedAt(Instant.now())
                .build();
    }
}
