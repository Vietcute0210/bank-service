package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.AccountRequest;
import com.vietphan.bank_service.DTO.response.AccountResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.AccountMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CardRepository cardRepository;
    private final BalanceRepository balanceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.ttl-minutes:10}")
    private long cacheTtlMinutes;

    @Override
    public AccountResponse getAccountById(Long accountId) {
        String cacheKey = RedisConstants.ACCOUNT_CACHE_PREFIX + accountId;
        try{
            Object cache = redisTemplate.opsForValue().get(cacheKey);
            if(cache instanceof AccountResponse cachedAccount){
                return cachedAccount;
            }
        }catch (Exception e){
        }
        AccountResponse response = accountRepository.findById(accountId)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        try{
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(cacheTtlMinutes));
        } catch (Exception e) {
        }
        return response;
    }


    @Override
    @Transactional
    public AccountResponse createAccount(AccountRequest accountRequest) {
        if (accountRequest.getEmail() != null && accountRepository.existsByEmail(accountRequest.getEmail())) {
            throw new AppException(Errors.ACCOUNT_ALREADY_EXISTS);
        }

        Account account = accountMapper.toEntity(accountRequest);
        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        if (account.getAccountType() == null) {
            account.setAccountType(AccountType.PAYMENT);
        }
        if (account.getCurrency() == null || account.getCurrency().isBlank()) {
            account.setCurrency("VND");
        }

        Account savedAccount = accountRepository.save(account);

        Balance initialBalance = Balance.builder()
                .account(savedAccount)
                .availableBalance(0.0)
                .holdBalance(0.0)
                .build();
        balanceRepository.save(initialBalance);

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountRequest accountRequest) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        if (accountRequest.getEmail() != null && !accountRequest.getEmail().isBlank()) {
            if (!accountRequest.getEmail().equalsIgnoreCase(account.getEmail())
                    && accountRepository.existsByEmail(accountRequest.getEmail())) {
                throw new AppException(Errors.ACCOUNT_ALREADY_EXISTS);
            }
            account.setEmail(accountRequest.getEmail());
        }

        if (accountRequest.getPhoneNumber() != null && !accountRequest.getPhoneNumber().isBlank()) {
            account.setPhoneNumber(accountRequest.getPhoneNumber());
        }

        if (accountRequest.getCustomerName() != null && !accountRequest.getCustomerName().isBlank()) {
            account.setCustomerName(accountRequest.getCustomerName());
        }

        Account updatedAccount = accountRepository.save(account);
        AccountResponse responex = accountMapper.toResponse(updatedAccount);

        // xoa cache cu o redis de con cap nhat du lieu moi
        try {
            redisTemplate.delete(RedisConstants.ACCOUNT_CACHE_PREFIX + accountId);
        } catch (Exception e) {
            System.err.println("Redis error on delete: " + e.getMessage());
        }

        return responex;
    }

    @Override
    @Transactional
    public AccountResponse deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        if (cardRepository.existsByAccount(account)) {
            throw new AppException(Errors.ACCOUNT_HAS_LINKED_CARDS);
        }

        Optional<Balance> balanceOpt = balanceRepository.findByAccount(account);
        if (balanceOpt.isPresent()) {
            Balance balance = balanceOpt.get();
            if (balance.getAvailableBalance() != 0.0 || balance.getHoldBalance() != 0.0) {
                throw new AppException(Errors.ACCOUNT_HAS_NON_ZERO_BALANCE);
            }
        }

        account.setStatus(AccountStatus.INACTIVE);
        Account deletedAccount = accountRepository.save(account);

        // Xóa cache trong Redis
        try {
            redisTemplate.delete(RedisConstants.ACCOUNT_CACHE_PREFIX + accountId);
        } catch (Exception e) {
            System.err.println("Redis error on delete: " + e.getMessage());
        }

        return accountMapper.toResponse(deletedAccount);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponse)
                .toList();
    }
}
