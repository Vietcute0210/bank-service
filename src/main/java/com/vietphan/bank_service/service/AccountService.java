package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.AccountRequest;
import com.vietphan.bank_service.DTO.response.AccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse getAccountById(UUID accountId);

    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse updateAccount(UUID accountId, AccountRequest accountRequest);

    AccountResponse deleteAccount(UUID accountId);

    List<AccountResponse> getAllAccounts();
}
