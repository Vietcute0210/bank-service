package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.BalanceService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    public BaseResponse<BalanceResponse> getMyBalance() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        var result = balanceService.getBalance(accountId);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Get balance successfully")
                .data(result)
                .build();
    }

    @PostMapping("/add")
    public BaseResponse<BalanceResponse> addBalance(@RequestParam("money") double money) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        var result = balanceService.addBalance(accountId, money);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Add balance successfully")
                .data(result)
                .build();
    }

    @PostMapping("/sub")
    public BaseResponse<BalanceResponse> subBalance(@RequestParam("money") double money) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        var result = balanceService.subtractBalance(accountId, money);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Subtract balance successfully")
                .data(result)
                .build();
    }
}