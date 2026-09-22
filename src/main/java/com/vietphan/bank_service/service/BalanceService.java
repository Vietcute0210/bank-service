package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.response.BalanceResponse;

import java.util.UUID;

public interface BalanceService {
    BalanceResponse getBalance(UUID accountId);

    BalanceResponse addBalance(UUID accountId, double money);

    BalanceResponse subtractBalance(UUID accountId, double money);
}
