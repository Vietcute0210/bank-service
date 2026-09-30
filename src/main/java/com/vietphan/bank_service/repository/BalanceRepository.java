package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByAccount(Account account);

    @Query("SELECT COALESCE(SUM(b.availableBalance), 0.0) FROM Balance b")
    Double sumTotalBalance();
}
