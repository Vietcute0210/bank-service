package com.vietphan.bank_service.entity;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_card_account_id", columnList = "account_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", updatable = false, nullable = false)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 20)
    private CardType cardType;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CardStatus status = CardStatus.ACTIVE;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    @Column(name = "has_pending_transactions")
    @Builder.Default
    private boolean hasPendingTransactions = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
