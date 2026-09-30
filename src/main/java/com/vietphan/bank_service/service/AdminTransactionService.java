package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.AdminUpdateTransactionStatusRequest;
import com.vietphan.bank_service.DTO.response.AdminTransactionResponse;

public interface AdminTransactionService {
    AdminTransactionResponse getAllTransactions(int page, int size);
    AdminTransactionResponse getTransactionById(Long id);
    AdminTransactionResponse updateTransactionStatus(Long id, AdminUpdateTransactionStatusRequest request);
}
