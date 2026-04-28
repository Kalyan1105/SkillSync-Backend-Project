package com.example.user_service.config;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "kalyan12218949@gmail.com";
        
        // Ensure only one admin exists by checking the specific admin email
        Optional<User> existingUser = userRepository.findByEmail(adminEmail);
        
        if (existingUser.isEmpty()) {
            User admin = User.builder()
                    .name("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("123@Kalyan"))
                    .role("ROLE_ADMIN")
                    .isEnabled(true)
                    .isMentorApproved(true)
                    .build();
            
            userRepository.save(admin);
            System.out.println("Admin user initialized successfully with email: " + adminEmail);
        } else {
            System.out.println("Admin user already exists with email: " + adminEmail);
        }
    }
}
