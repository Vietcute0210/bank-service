package com.vietphan.payment_service.DTO.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private Long accountId;
    private double amount;
    private String currency;
    private String status;
    private Instant timestamp;
}
