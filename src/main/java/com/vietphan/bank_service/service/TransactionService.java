package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.response.TransactionResponse;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getMyTransactions(Long accountId);

    List<TransactionResponse> getTransactionsByCard(Long accountId, Long cardId);

    TransactionResponse getTransaction(Long id);
}
