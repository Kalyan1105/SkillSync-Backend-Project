package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationRequestDTO {
    private String email;
    private String otp;
}
