package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.UserLevelRequest;
import com.vietphan.bank_service.DTO.response.UserLevelResponse;
import com.vietphan.bank_service.entity.UserLevel;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.UserLevelMapper;
import com.vietphan.bank_service.repository.UserLevelRepository;
import com.vietphan.bank_service.repository.UserRepository;
import com.vietphan.bank_service.service.UserLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLevelServiceImpl implements UserLevelService {

    private final UserLevelRepository userLevelRepository;
    private final UserRepository userRepository;
    private final UserLevelMapper userLevelMapper;

    @Override
    @Transactional
    public UserLevelResponse createLevel(UserLevelRequest request) {
        validateRequest(request);

        String levelName = request.getLevelName().trim();
        if (userLevelRepository.existsByLevelName(levelName)) {
            throw new AppException(Errors.USER_LEVEL_ALREADY_EXISTS);
        }

        UserLevel userLevel = userLevelMapper.toEntity(request);
        userLevel.setLevelName(levelName);
        UserLevel savedLevel = userLevelRepository.save(userLevel);

        return userLevelMapper.toResponse(savedLevel);
    }

    @Override
    public List<UserLevelResponse> getAllLevels() {
        return userLevelRepository.findAll().stream()
                .map(userLevelMapper::toResponse)
                .toList();
    }

    @Override
    public UserLevelResponse getLevelById(Long id) {
        UserLevel level = userLevelRepository.findById(id)
                .orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND));
        return userLevelMapper.toResponse(level);
    }

    @Override
    @Transactional
    public UserLevelResponse updateLevel(Long id, UserLevelRequest request) {
        validateRequest(request);

        UserLevel level = userLevelRepository.findById(id)
                .orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND));

        String newName = request.getLevelName().trim();
        if (userLevelRepository.existsByLevelNameAndLevelIdNot(newName, id)) {
            throw new AppException(Errors.USER_LEVEL_ALREADY_EXISTS);
        }

        userLevelMapper.updateEntity(level, request);
        level.setLevelName(newName);
        UserLevel updatedLevel = userLevelRepository.save(level);

        return userLevelMapper.toResponse(updatedLevel);
    }

    @Override
    @Transactional
    public void deleteLevel(Long id) {
        UserLevel level = userLevelRepository.findById(id)
                .orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND));

        if (userRepository.existsByLevel(level)) {
            throw new AppException(Errors.USER_LEVEL_IN_USE);
        }

        userLevelRepository.delete(level);
    }

    private void validateRequest(UserLevelRequest request) {
        if (request == null || request.getLevelName() == null || request.getLevelName().trim().isBlank()) {
            throw new AppException(Errors.INVALID_USER_LEVEL_DATA);
        }
        if (request.getCardLimit() <= 0 || request.getDailyTransferLimit() <= 0) {
            throw new AppException(Errors.INVALID_USER_LEVEL_DATA);
        }
    }
}
