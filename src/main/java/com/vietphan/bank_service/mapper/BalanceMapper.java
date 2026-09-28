package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.request.BalanceRequest;
import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper {

    public Balance toEntity(BalanceRequest request, Account account) {
        if (request == null) {
            return null;
        }

        return Balance.builder()
                .account(account)
                .availableBalance(request.getAvailableBalance())
                .holdBalance(request.getHoldBalance())
                .build();
    }

    public BalanceResponse toResponse(Balance balance) {
        if (balance == null) {
            return null;
        }

        return BalanceResponse.builder()
                .balanceId(balance.getBalanceId())
                .accountId(balance.getAccount() != null ? balance.getAccount().getAccountId() : null)
                .availableBalance(balance.getAvailableBalance())
                .holdBalance(balance.getHoldBalance())
                .createdAt(balance.getCreatedAt())
                .updatedAt(balance.getUpdatedAt())
                .build();
    }

    public void updateBalance(Balance balance, BalanceRequest request) {
        if (balance == null || request == null) {
            return;
        }

        balance.setAvailableBalance(request.getAvailableBalance());
        balance.setHoldBalance(request.getHoldBalance());
    }
}
