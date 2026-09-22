package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.response.BalanceResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.BalanceMapper;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Override
    public BalanceResponse getBalance(UUID accountId) {
        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        Balance balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        return balanceMapper.toResponse(balance);
    }

    @Override
    @Transactional
    public BalanceResponse addBalance(UUID accountId, double money) {
        if (money <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        Balance balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        balance.setAvailableBalance(balance.getAvailableBalance() + money);
        Balance updatedBalance = balanceRepository.save(balance);

        return balanceMapper.toResponse(updatedBalance);
    }

    @Override
    @Transactional
    public BalanceResponse subtractBalance(UUID accountId, double money) {
        if (money <= 0) {
            throw new AppException(Errors.INVALID_AMOUNT);
        }

        if (accountId == null) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        Balance balance = balanceRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(Errors.BALANCE_NOT_FOUND));

        if (balance.getAvailableBalance() < money) {
            throw new AppException(Errors.INSUFFICIENT_FUNDS);
        }

        balance.setAvailableBalance(balance.getAvailableBalance() - money);
        Balance updatedBalance = balanceRepository.save(balance);

        return balanceMapper.toResponse(updatedBalance);
    }
}
