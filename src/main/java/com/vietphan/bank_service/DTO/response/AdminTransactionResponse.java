package com.vietphan.bank_service.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vietphan.bank_service.enums.TransactionStatus;
import com.vietphan.bank_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminTransactionResponse {

    private Long transactionId;
    private String fromCardNumber;
    private String toCardNumber;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private Instant createdAt;
    private String fromUserName;
    private String toUserName;
    private List<String> actions;

    // Pagination wrapper fields
    private List<AdminTransactionResponse> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
}
