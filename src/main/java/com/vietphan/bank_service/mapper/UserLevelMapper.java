package com.vietphan.bank_service.mapper;

import com.vietphan.bank_service.DTO.request.UserLevelRequest;
import com.vietphan.bank_service.DTO.response.UserLevelResponse;
import com.vietphan.bank_service.entity.UserLevel;
import org.springframework.stereotype.Component;

@Component
public class UserLevelMapper {

    public UserLevel toEntity(UserLevelRequest request) {
        if (request == null) {
            return null;
        }

        return UserLevel.builder()
                .levelName(request.getLevelName() != null ? request.getLevelName().trim() : null)
                .cardLimit(request.getCardLimit())
                .dailyTransferLimit(request.getDailyTransferLimit())
                .build();
    }

    public UserLevelResponse toResponse(UserLevel userLevel) {
        if (userLevel == null) {
            return null;
        }

        return UserLevelResponse.builder()
                .levelId(userLevel.getLevelId())
                .levelName(userLevel.getLevelName())
                .cardLimit(userLevel.getCardLimit())
                .dailyTransferLimit(userLevel.getDailyTransferLimit())
                .createdAt(userLevel.getCreatedAt())
                .updatedAt(userLevel.getUpdatedAt())
                .build();
    }

    public void updateEntity(UserLevel userLevel, UserLevelRequest request) {
        if (userLevel == null || request == null) {
            return;
        }

        if (request.getLevelName() != null && !request.getLevelName().isBlank()) {
            userLevel.setLevelName(request.getLevelName().trim());
        }
        if (request.getCardLimit() > 0) {
            userLevel.setCardLimit(request.getCardLimit());
        }
        if (request.getDailyTransferLimit() > 0) {
            userLevel.setDailyTransferLimit(request.getDailyTransferLimit());
        }
    }
}
