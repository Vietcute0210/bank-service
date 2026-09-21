package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.AccountRequest;
import com.vietphan.bank_service.DTO.response.AccountResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public BaseResponse<AccountResponse> getAccountById(@PathVariable("id") UUID id) {
        AccountResponse response = accountService.getAccountById(id);
        return BaseResponse.<AccountResponse>builder()
                .code(1000)
                .message("Get account successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public BaseResponse<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> responses = accountService.getAllAccounts();
        return BaseResponse.<List<AccountResponse>>builder()
                .code(1000)
                .message("Get all accounts successfully")
                .data(responses)
                .build();
    }

    @PostMapping
    public BaseResponse<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return BaseResponse.<AccountResponse>builder()
                .code(1000)
                .message("Create account successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public BaseResponse<AccountResponse> updateAccount(
            @PathVariable("id") UUID id,
            @RequestBody AccountRequest request
    ) {
        AccountResponse response = accountService.updateAccount(id, request);
        return BaseResponse.<AccountResponse>builder()
                .code(1000)
                .message("Update account successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<AccountResponse> deleteAccount(@PathVariable("id") UUID id) {
        AccountResponse response = accountService.deleteAccount(id);
        return BaseResponse.<AccountResponse>builder()
                .code(1000)
                .message("Delete account successfully")
                .data(response)
                .build();
    }
}
