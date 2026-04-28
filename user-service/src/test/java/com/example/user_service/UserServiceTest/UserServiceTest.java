package com.example.user_service.UserServiceTest;

import com.example.user_service.dto.UpdateProfileDTO;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private UserService userService;

    private void mockSecurity(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getUserById_success() {
        User user = User.builder().userId(1L).email("a@gmail.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Object result = userService.getUserById(1L);

        assertNotNull(result);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_success() {
        mockSecurity("a@gmail.com");

        User logged = User.builder().userId(1L).email("a@gmail.com").build();
        User user = User.builder().userId(1L).email("a@gmail.com").build();

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(logged));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateProfileDTO dto = new UpdateProfileDTO("new", "java", 2);

        userService.updateUser(1L, dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_unauthorized() {
        mockSecurity("a@gmail.com");

        User logged = User.builder().userId(2L).email("a@gmail.com").build();

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(logged));

        UpdateProfileDTO dto = new UpdateProfileDTO("new", "java", 2);

        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void getUserByEmail_success() {
        User user = User.builder().email("a@gmail.com").build();

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("a@gmail.com");

        assertEquals("a@gmail.com", result.getEmail());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}
