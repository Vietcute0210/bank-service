package com.vietphan.bank_service.service;

import com.vietphan.bank_service.DTO.request.AdminCreateUserRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateUserRequest;
import com.vietphan.bank_service.DTO.response.AdminUserDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminUserResponse;
import com.vietphan.bank_service.enums.Role;

import java.util.List;

public interface AdminUserService {
    List<AdminUserResponse> getAllUsers(String levelName, Role role);
    AdminUserDetailResponse getUserDetail(Long userId);
    AdminUserResponse createUser(AdminCreateUserRequest request);
    AdminUserResponse updateUser(Long userId, AdminUpdateUserRequest request);
    void deleteUser(Long userId);
}
