package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.UserLevelRequest;
import com.vietphan.bank_service.DTO.response.UserLevelResponse;

import java.util.List;

public interface UserLevelService {

    UserLevelResponse createLevel(UserLevelRequest request);

    List<UserLevelResponse> getAllLevels();

    UserLevelResponse getLevelById(Long id);

    UserLevelResponse updateLevel(Long id, UserLevelRequest request);

    void deleteLevel(Long id);
}
