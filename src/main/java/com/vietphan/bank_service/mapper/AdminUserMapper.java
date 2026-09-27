package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.response.AdminUserDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminUserResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.entity.Card;
import com.vietphan.bank_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminUserMapper {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final CardMapper cardMapper;

    public AdminUserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        Account account = user.getAccount();
        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(account != null ? account.getCustomerName() : null)
                .email(account != null ? account.getEmail() : null)
                .phoneNumber(account != null ? account.getPhoneNumber() : null)
                .role(user.getRole())
                .levelId(user.getLevel() != null ? user.getLevel().getLevelId() : null)
                .levelName(user.getLevel() != null ? user.getLevel().getLevelName() : null)
                .accountId(account != null ? account.getAccountId() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AdminUserDetailResponse toDetailResponse(User user, Balance balance, List<Card> cards) {
        if (user == null) {
            return null;
        }

        Account account = user.getAccount();
        var cardResponses = (cards != null)
                ? cards.stream().map(cardMapper::toResponse).toList()
                : Collections.<com.vietphan.bank_service.DTO.response.CardResponse>emptyList();

        return AdminUserDetailResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(account != null ? account.getCustomerName() : null)
                .email(account != null ? account.getEmail() : null)
                .phoneNumber(account != null ? account.getPhoneNumber() : null)
                .role(user.getRole())
                .levelId(user.getLevel() != null ? user.getLevel().getLevelId() : null)
                .levelName(user.getLevel() != null ? user.getLevel().getLevelName() : null)
                .account(account != null ? accountMapper.toResponse(account) : null)
                .balance(balance != null ? balanceMapper.toResponse(balance) : null)
                .cards(cardResponses)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
