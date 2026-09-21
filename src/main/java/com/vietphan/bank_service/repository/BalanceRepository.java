package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    Optional<Balance> findByAccount(Account account);
}
