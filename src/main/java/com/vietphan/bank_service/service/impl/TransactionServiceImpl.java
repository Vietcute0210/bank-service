package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.entity.Transaction;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.TransactionMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

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
}
