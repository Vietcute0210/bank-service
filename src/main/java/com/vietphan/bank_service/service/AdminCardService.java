package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.AdminCreateCardRequest;
import com.vietphan.bank_service.DTO.request.AdminDepositWithdrawRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateCardRequest;
import com.vietphan.bank_service.DTO.response.AdminCardDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminCardResponse;

import java.util.List;

public interface AdminCardService {
    List<AdminCardResponse> getAllCards();
    AdminCardDetailResponse getCardDetail(Long cardId);
    AdminCardResponse createCard(AdminCreateCardRequest request);
    AdminCardResponse updateCard(Long cardId, AdminUpdateCardRequest request);
    void deleteCard(Long cardId);
    AdminCardDetailResponse depositToCard(Long cardId, AdminDepositWithdrawRequest request);
    AdminCardDetailResponse withdrawFromCard(Long cardId, AdminDepositWithdrawRequest request);
}
