package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.request.AccountRequest;
import com.vietphan.bank_service.DTO.response.AccountResponse;
import com.vietphan.bank_service.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequest request) {
        if (request == null) {
            return null;
        }

        return Account.builder()
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .accountType(request.getAccountType())
                .status(request.getStatus())
                .currency(request.getCurrency())
                .build();
    }

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .customerName(account.getCustomerName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public void updateAccount(Account account, AccountRequest request) {
        if (account == null || request == null) {
            return;
        }

        if (request.getCustomerName() != null) {
            account.setCustomerName(request.getCustomerName());
        }
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            account.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }
        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }
        if (request.getCurrency() != null) {
            account.setCurrency(request.getCurrency());
        }
    }
}
