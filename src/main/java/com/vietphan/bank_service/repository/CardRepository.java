package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByAccount(Account account);
    Optional<Card> findByCardNumber(String cardNumber);
    boolean existsByAccount(Account account);
    boolean existsByCardNumber(String cardNumber);
}
