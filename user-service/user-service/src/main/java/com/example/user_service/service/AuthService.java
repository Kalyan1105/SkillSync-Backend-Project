

package com.example.user_service.service;

import com.example.user_service.dto.*;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public void register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_LEARNER");
        user.setIsMentorApproved(false);

        userRepository.save(user);
    }


    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new LoginResponseDTO("Login successful", accessToken, refreshToken);
    }

    public LoginResponseDTO refreshToken(TokenRefreshRequestDTO request) {
        String token = request.getRefreshToken();
        
        try {
            String email = jwtUtil.extractEmail(token);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!jwtUtil.isTokenValid(token, user.getEmail())) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            
            return new LoginResponseDTO("Token refreshed successfully", newAccessToken, newRefreshToken);
            
        } catch (Exception e) {
            throw new RuntimeException("Refresh token is expired or invalid");
        }
    }
}