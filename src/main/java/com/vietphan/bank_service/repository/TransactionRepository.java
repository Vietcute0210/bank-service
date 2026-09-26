package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromCardNumberOrToCardNumber(String from, String to);

    @Query("SELECT t FROM Transaction t WHERE t.fromCardNumber = :cardNumber OR t.toCardNumber = :cardNumber ORDER BY t.createdAt DESC")
    List<Transaction> findByCardNumber(@Param("cardNumber") String cardNumber);

    @Query("SELECT t FROM Transaction t WHERE t.fromCardNumber IN :cardNumbers OR t.toCardNumber IN :cardNumbers ORDER BY t.createdAt DESC")
    List<Transaction> findByCardNumbers(@Param("cardNumbers") List<String> cardNumbers);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t " +
           "WHERE t.fromCardNumber IN :cardNumbers " +
           "AND t.transactionType = com.vietphan.bank_service.enums.TransactionType.TRANSFER " +
           "AND t.status = com.vietphan.bank_service.enums.TransactionStatus.SUCCESS " +
           "AND t.createdAt >= :startOfDay")
    double sumDailyTransfers(@Param("cardNumbers") List<String> cardNumbers, @Param("startOfDay") java.time.Instant startOfDay);

    Page<Transaction> findAll(Pageable pageable);
}
