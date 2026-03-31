package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String message;
    private String token;
    private String refreshToken;
}