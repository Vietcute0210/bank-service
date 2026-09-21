package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.CardRequest;
import com.vietphan.bank_service.DTO.response.CardResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {
    CardResponse createCard(CardRequest request);

    List<CardResponse> getCardsByAccountId(UUID accountId);

    CardResponse getCardById(UUID cardId);

    CardResponse deleteCard(UUID cardId);

}
