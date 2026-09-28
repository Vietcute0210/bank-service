package com.vietphan.payment_service.repository;

import com.vietphan.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}
