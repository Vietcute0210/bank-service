package com.vietphan.bank_service.DTO.response;

import com.vietphan.bank_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Long levelId;
    private String levelName;
    private Long accountId;
    private Instant createdAt;
    private Instant updatedAt;
}
