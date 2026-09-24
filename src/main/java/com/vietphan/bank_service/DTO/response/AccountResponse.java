package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID accountId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private AccountType accountType;
    private AccountStatus status;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;
}
