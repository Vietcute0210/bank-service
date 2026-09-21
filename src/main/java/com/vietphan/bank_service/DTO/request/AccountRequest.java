package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    private String customerName;
    private String email;
    private String phoneNumber;
    private AccountType accountType;
    private AccountStatus status;
    private String currency;
}
