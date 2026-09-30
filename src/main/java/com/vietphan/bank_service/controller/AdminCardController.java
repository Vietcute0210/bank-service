package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.AdminCreateCardRequest;
import com.vietphan.bank_service.DTO.request.AdminDepositWithdrawRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateCardRequest;
import com.vietphan.bank_service.DTO.response.AdminCardDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminCardResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.service.AdminCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping
    public BaseResponse<List<AdminCardResponse>> getAllCards() {
        List<AdminCardResponse> response = adminCardService.getAllCards();
        return BaseResponse.<List<AdminCardResponse>>builder()
                .code(1000)
                .message("Get all cards successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<AdminCardDetailResponse> getCardDetail(@PathVariable("id") Long id) {
        AdminCardDetailResponse response = adminCardService.getCardDetail(id);
        return BaseResponse.<AdminCardDetailResponse>builder()
                .code(1000)
                .message("Get card detail successfully")
                .data(response)
                .build();
    }

    @PostMapping
    public BaseResponse<AdminCardResponse> createCard(@RequestBody AdminCreateCardRequest request) {
        AdminCardResponse response = adminCardService.createCard(request);
        return BaseResponse.<AdminCardResponse>builder()
                .code(1000)
                .message("Create card successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public BaseResponse<AdminCardResponse> updateCard(
            @PathVariable("id") Long id,
            @RequestBody AdminUpdateCardRequest request
    ) {
        AdminCardResponse response = adminCardService.updateCard(id, request);
        return BaseResponse.<AdminCardResponse>builder()
                .code(1000)
                .message("Update card successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteCard(@PathVariable("id") Long id) {
        adminCardService.deleteCard(id);
        return BaseResponse.<Void>builder()
                .code(1000)
                .message("Delete card successfully")
                .build();
    }

    @PostMapping("/{id}/deposit")
    public BaseResponse<AdminCardDetailResponse> depositToCard(
            @PathVariable("id") Long id,
            @RequestBody AdminDepositWithdrawRequest request
    ) {
        AdminCardDetailResponse response = adminCardService.depositToCard(id, request);
        return BaseResponse.<AdminCardDetailResponse>builder()
                .code(1000)
                .message("Deposit to card successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{id}/withdraw")
    public BaseResponse<AdminCardDetailResponse> withdrawFromCard(
            @PathVariable("id") Long id,
            @RequestBody AdminDepositWithdrawRequest request
    ) {
        AdminCardDetailResponse response = adminCardService.withdrawFromCard(id, request);
        return BaseResponse.<AdminCardDetailResponse>builder()
                .code(1000)
                .message("Withdraw from card successfully")
                .data(response)
                .build();
    }
}
