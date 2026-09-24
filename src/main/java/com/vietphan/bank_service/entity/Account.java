package com.vietphan.bank_service.entity;

import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_account_email", columnList = "email"),
        @Index(name = "idx_account_phone", columnList = "phone_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", updatable = false, nullable = false)
    private Long accountId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    @Builder.Default
    private AccountType accountType = AccountType.PAYMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "VND";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
