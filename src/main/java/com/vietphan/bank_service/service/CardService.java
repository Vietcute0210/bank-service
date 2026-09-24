package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.CardRequest;
import com.vietphan.bank_service.DTO.response.CardResponse;

import java.util.List;

public interface CardService {
    CardResponse createCard(CardRequest request);

    List<CardResponse> getCardsByAccountId(Long accountId);

    CardResponse getCardById(Long cardId);

    CardResponse deleteCard(Long cardId);

}
