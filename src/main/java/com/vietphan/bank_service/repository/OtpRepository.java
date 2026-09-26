package com.vietphan.bank_service.repository;

import com.vietphan.bank_service.entity.Otp;
import com.vietphan.bank_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByUserAndVerifiedFalseOrderByCreatedAtDesc(User user);

    Optional<Otp> findTopByUserAndOtpCodeAndVerifiedFalseOrderByCreatedAtDesc(User user, String otpCode);

    void deleteByUser(User user);
}
