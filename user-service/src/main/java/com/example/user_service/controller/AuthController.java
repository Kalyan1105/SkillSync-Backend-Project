package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return new UserResponseDTO("User registered successfully. Please check your email for the verification OTP.");
    }

    @PostMapping("/verify-registration")
    public UserResponseDTO verifyRegistration(@RequestBody OtpVerificationRequestDTO request) {
        authService.verifyRegistration(request);
        return new UserResponseDTO("Account verified successfully. You can now login.");
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public UserResponseDTO forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return new UserResponseDTO("OTP sent to your email for password reset.");
    }

    @PostMapping("/reset-password")
    public UserResponseDTO resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return new UserResponseDTO("Password reset successfully. You can now login with your new password.");
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        return authService.refreshToken(request);
    }
}