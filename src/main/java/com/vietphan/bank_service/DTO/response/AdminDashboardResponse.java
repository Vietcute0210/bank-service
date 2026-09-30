package com.vietphan.bank_service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long totalCards;
    private Long totalTransactions;
    private Double totalBalance;
    private List<TransactionResponse> recentTransactions;
}
