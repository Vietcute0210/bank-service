package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.CardSearchResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;

public interface TransferService {

    CardSearchResponse searchCard(String cardNumber);

    TransferInitiateResponse initiateTransfer(Long accountId, TransferInitiateRequest request);

    TransactionResponse confirmTransfer(Long accountId, TransferConfirmRequest request);
}
