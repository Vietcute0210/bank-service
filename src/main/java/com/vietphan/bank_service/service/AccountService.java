package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.AccountRequest;
import com.vietphan.bank_service.DTO.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse getAccountById(Long accountId);

    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse updateAccount(Long accountId, AccountRequest accountRequest);

    AccountResponse deleteAccount(Long accountId);

    List<AccountResponse> getAllAccounts();
}
