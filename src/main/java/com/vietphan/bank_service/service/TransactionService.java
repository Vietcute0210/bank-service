package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferConfirmResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getMyTransactions(Long accountId);

    List<TransactionResponse> getTransactionsByCard(Long accountId, Long cardId);

    TransactionResponse getTransaction(Long id);

    TransferInitiateResponse initiateTransfer(Long accountId, TransferInitiateRequest request);

    TransferConfirmResponse confirmTransfer(Long accountId, TransferConfirmRequest request);
}
