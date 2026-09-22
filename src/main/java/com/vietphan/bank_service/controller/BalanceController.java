package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    public BaseResponse<BalanceResponse> getBalance(@PathVariable("accountId") UUID accountId) {
        var result = balanceService.getBalance(accountId);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Get balance successfully")
                .data(result)
                .build();
    }

    @PostMapping("/{accountId}/add")
    public BaseResponse<BalanceResponse> addBalance(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("money") double money
    ) {
        var result = balanceService.addBalance(accountId, money);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Add balance successfully")
                .data(result)
                .build();
    }

    @PostMapping("/{accountId}/sub")
    public BaseResponse<BalanceResponse> subBalance(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("money") double money
    ) {
        var result = balanceService.subtractBalance(accountId, money);
        return BaseResponse.<BalanceResponse>builder()
                .code(1000)
                .message("Subtract balance successfully")
                .data(result)
                .build();
    }
}