package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.response.AdminDashboardResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public BaseResponse<AdminDashboardResponse> getDashboard() {
        AdminDashboardResponse response = adminDashboardService.getDashboard();
        return BaseResponse.<AdminDashboardResponse>builder()
                .code(1000)
                .message("Get admin dashboard data successfully")
                .data(response)
                .build();
    }
}
