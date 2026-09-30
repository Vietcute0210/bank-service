package com.vietphan.bank_service.DTO.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpMessage implements Serializable {

    private String email;
    private String otpCode;
    private String purpose;
}
