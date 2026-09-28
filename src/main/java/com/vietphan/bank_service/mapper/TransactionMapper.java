package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.response.TransactionResponse;
import com.vietphan.bank_service.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromCardNumber(transaction.getFromCardNumber())
                .toCardNumber(transaction.getToCardNumber())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
