package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.RefreshToken;
import com.vietphan.bank_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}