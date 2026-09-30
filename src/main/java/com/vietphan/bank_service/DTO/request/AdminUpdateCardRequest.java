package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.CardStatus;
import com.vietphan.bank_service.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateCardRequest {
    private CardStatus status;
    private CardType cardType;
}
