package com.vietphan.bank_service.DTO.request;

import com.vietphan.bank_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateUserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Long levelId;
}
