package com.example.user_service.UserServiceTest;

import com.example.user_service.dto.*;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;

import com.example.user_service.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.example.user_service.service.EmailService emailService;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequestDTO req = new RegisterRequestDTO("kalyan", "k@gmail.com", "123");

        when(userRepository.findByEmail("k@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded");

        authService.register(req);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_duplicateEmail() {
        RegisterRequestDTO req = new RegisterRequestDTO("kalyan", "k@gmail.com", "123");

        when(userRepository.findByEmail("k@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void login_success() {
        LoginRequestDTO req = new LoginRequestDTO("k@gmail.com", "123");

        User user = User.builder()
                .email("k@gmail.com")
                .password("encoded")
                .role("ROLE_LEARNER")
                .isEnabled(true)
                .build();

        when(userRepository.findByEmail("k@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded")).thenReturn(true);

        LoginResponseDTO res = authService.login(req);

        assertEquals("Login successful", res.getMessage());
    }

    @Test
    void login_wrongPassword() {
        LoginRequestDTO req = new LoginRequestDTO("k@gmail.com", "123");

        User user = User.builder()
                .email("k@gmail.com")
                .password("encoded")
                .isEnabled(true)
                .build();

        when(userRepository.findByEmail("k@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail("k@gmail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequestDTO("k@gmail.com", "123")));
    }
    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}