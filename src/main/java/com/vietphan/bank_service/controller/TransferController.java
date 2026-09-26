package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.TransferConfirmRequest;
import com.vietphan.bank_service.DTO.request.TransferInitiateRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.CardSearchResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.DTO.response.TransferInitiateResponse;
import com.vietphan.bank_service.service.TransferService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/search-card")
    public BaseResponse<CardSearchResponse> searchCard(@RequestParam("cardNumber") String cardNumber) {
        CardSearchResponse response = transferService.searchCard(cardNumber);
        return BaseResponse.<CardSearchResponse>builder()
                .code(1000)
                .message("Search card successfully")
                .data(response)
                .build();
    }

    @PostMapping("/initiate")
    public BaseResponse<TransferInitiateResponse> initiateTransfer(@RequestBody TransferInitiateRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        TransferInitiateResponse response = transferService.initiateTransfer(accountId, request);
        return BaseResponse.<TransferInitiateResponse>builder()
                .code(1000)
                .message("Initiate transfer successfully")
                .data(response)
                .build();
    }

    @PostMapping("/confirm")
    public BaseResponse<TransactionResponse> confirmTransfer(@RequestBody TransferConfirmRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        TransactionResponse response = transferService.confirmTransfer(accountId, request);
        return BaseResponse.<TransactionResponse>builder()
                .code(1000)
                .message("Transfer completed successfully")
                .data(response)
                .build();
    }
}
