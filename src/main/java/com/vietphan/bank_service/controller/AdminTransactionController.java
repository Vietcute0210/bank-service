package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.AdminUpdateTransactionStatusRequest;
import com.vietphan.bank_service.DTO.response.AdminTransactionResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.AdminTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    @GetMapping
    public BaseResponse<AdminTransactionResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AdminTransactionResponse response = adminTransactionService.getAllTransactions(page, size);
        return BaseResponse.<AdminTransactionResponse>builder()
                .code(1000)
                .message("Get all transactions successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<AdminTransactionResponse> getTransactionById(@PathVariable("id") Long id) {
        AdminTransactionResponse response = adminTransactionService.getTransactionById(id);
        return BaseResponse.<AdminTransactionResponse>builder()
                .code(1000)
                .message("Get transaction details successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}/status")
    public BaseResponse<AdminTransactionResponse> updateTransactionStatus(
            @PathVariable("id") Long id,
            @RequestBody(required = false) AdminUpdateTransactionStatusRequest request
    ) {
        AdminTransactionResponse response = adminTransactionService.updateTransactionStatus(id, request);
        return BaseResponse.<AdminTransactionResponse>builder()
                .code(1000)
                .message("Update transaction status successfully")
                .data(response)
                .build();
    }
}
