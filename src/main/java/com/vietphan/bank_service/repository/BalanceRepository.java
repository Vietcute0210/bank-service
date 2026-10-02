package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByAccount(Account account);
    Optional<Balance> findByCard(com.vietphan.bank_service.entity.Card card);

    @Query("SELECT COALESCE(SUM(b.availableBalance), 0.0) FROM Balance b")
    Double sumTotalBalance();
}
