package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
