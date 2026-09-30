package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.response.AdminDashboardResponse;
import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.entity.Transaction;
import com.vietphan.bank_service.mapper.TransactionMapper;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.CardRepository;
import com.vietphan.bank_service.repository.TransactionRepository;
import com.vietphan.bank_service.repository.UserRepository;
import com.vietphan.bank_service.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalCards = cardRepository.count();
        long totalTransactions = transactionRepository.count();

        Double totalBalance = balanceRepository.sumTotalBalance();
        if (totalBalance == null) {
            totalBalance = 0.0;
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> recentTxPage = transactionRepository.findAll(pageable);

        List<TransactionResponse> recentTransactions = recentTxPage.getContent().stream()
                .map(transactionMapper::toResponse)
                .toList();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalCards(totalCards)
                .totalTransactions(totalTransactions)
                .totalBalance(totalBalance)
                .recentTransactions(recentTransactions)
                .build();
    }
}
