package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.CardRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.CardResponse;
import com.vietphan.bank_service.service.CardService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public BaseResponse<CardResponse> createCard(@RequestBody CardRequest request) {
        request.setAccountId(SecurityUtils.getCurrentAccountId());
        CardResponse response = cardService.createCard(request);
        return BaseResponse.<CardResponse>builder()
                .code(1000)
                .message("Create card successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public BaseResponse<List<CardResponse>> getMyCards() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        List<CardResponse> responses = cardService.getCardsByAccountId(accountId);
        return BaseResponse.<List<CardResponse>>builder()
                .code(1000)
                .message("Get cards successfully")
                .data(responses)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<CardResponse> getCardById(@PathVariable("id") Long id) {
        CardResponse response = cardService.getCardById(id);
        return BaseResponse.<CardResponse>builder()
                .code(1000)
                .message("Get card successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<CardResponse> deleteCard(@PathVariable("id") Long id) {
        CardResponse response = cardService.deleteCard(id);
        return BaseResponse.<CardResponse>builder()
                .code(1000)
                .message("Delete card successfully")
                .data(response)
                .build();
    }

}
