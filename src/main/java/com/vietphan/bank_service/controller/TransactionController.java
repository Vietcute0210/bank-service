package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferConfirmResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;
import com.vietphan.bank_service.service.TransactionService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/transaction", "/api/v1/transactions"})
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public BaseResponse<List<TransactionResponse>> getMyTransactions() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        List<TransactionResponse> result = transactionService.getMyTransactions(accountId);
        return BaseResponse.<List<TransactionResponse>>builder()
                .code(1000)
                .message("Get transactions successfully")
                .data(result)
                .build();
    }

    @GetMapping("/card/{cardId}")
    public BaseResponse<List<TransactionResponse>> getTransactionsByCard(@PathVariable Long cardId) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        List<TransactionResponse> result = transactionService.getTransactionsByCard(accountId, cardId);
        return BaseResponse.<List<TransactionResponse>>builder()
                .code(1000)
                .message("Get card transactions successfully")
                .data(result)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<TransactionResponse> getTransaction(@PathVariable Long id) {
        TransactionResponse result = transactionService.getTransaction(id);
        return BaseResponse.<TransactionResponse>builder()
                .code(1000)
                .message("Get transaction successfully")
                .data(result)
                .build();
    }

    @PostMapping("/initiate")
    public BaseResponse<TransferInitiateResponse> initiateTransfer(@RequestBody TransferInitiateRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        TransferInitiateResponse response = transactionService.initiateTransfer(accountId, request);
        return BaseResponse.<TransferInitiateResponse>builder()
                .code(1000)
                .message("Transfer initiated successfully")
                .data(response)
                .build();
    }

    @PostMapping("/confirm")
    public BaseResponse<TransferConfirmResponse> confirmTransfer(@RequestBody TransferConfirmRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        TransferConfirmResponse response = transactionService.confirmTransfer(accountId, request);
        return BaseResponse.<TransferConfirmResponse>builder()
                .code(1000)
                .message("Transfer confirmed successfully")
                .data(response)
                .build();
    }
}
