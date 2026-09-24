package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.response.BalanceResponse;

public interface BalanceService {
    BalanceResponse getBalance(Long accountId);

    BalanceResponse addBalance(Long accountId, double money);

    BalanceResponse subtractBalance(Long accountId, double money);
}
