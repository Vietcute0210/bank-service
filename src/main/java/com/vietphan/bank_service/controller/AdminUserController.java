package com.vietphan.bank_service.controller;

import com.vietphan.bank_service.DTO.request.AdminCreateUserRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateUserRequest;
import com.vietphan.bank_service.DTO.response.AdminUserDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminUserResponse;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.enums.Role;
import com.vietphan.bank_service.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public BaseResponse<List<AdminUserResponse>> getAllUsers(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Role role
    ) {
        List<AdminUserResponse> users = adminUserService.getAllUsers(level, role);
        return BaseResponse.<List<AdminUserResponse>>builder()
                .code(1000)
                .message("Get all users successfully")
                .data(users)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<AdminUserDetailResponse> getUserDetail(@PathVariable Long id) {
        AdminUserDetailResponse response = adminUserService.getUserDetail(id);
        return BaseResponse.<AdminUserDetailResponse>builder()
                .code(1000)
                .message("Get user detail successfully")
                .data(response)
                .build();
    }

    @PostMapping
    public BaseResponse<AdminUserResponse> createUser(@RequestBody AdminCreateUserRequest request) {
        AdminUserResponse response = adminUserService.createUser(request);
        return BaseResponse.<AdminUserResponse>builder()
                .code(1000)
                .message("Create user successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public BaseResponse<AdminUserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpdateUserRequest request
    ) {
        AdminUserResponse response = adminUserService.updateUser(id, request);
        return BaseResponse.<AdminUserResponse>builder()
                .code(1000)
                .message("Update user successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return BaseResponse.<Void>builder()
                .code(1000)
                .message("Delete user successfully")
                .build();
    }
}
