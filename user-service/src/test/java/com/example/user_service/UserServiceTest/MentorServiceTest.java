package com.example.user_service.UserServiceTest;


import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.MentorService;
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
class MentorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private MentorService mentorService;

    private void mockSecurity(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void applyForMentor_success() {
        mockSecurity("a@gmail.com");

        User user = User.builder().email("a@gmail.com").build();

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));

        mentorService.applyForMentor();

        verify(userRepository).save(user);
    }

    @Test
    void applyForMentor_userNotFound() {
        mockSecurity("a@gmail.com");

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorService.applyForMentor());
    }

    @Test
    void approveMentor_success() {
        User user = User.builder().userId(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mentorService.approveMentor(1L);

        verify(userRepository).save(user);
    }

    @Test
    void approveMentor_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mentorService.approveMentor(1L));
    }

    @Test
    void updateAvailability_success() {
        mockSecurity("a@gmail.com");

        User user = User.builder().userId(1L).email("a@gmail.com").build();

        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));

        mentorService.updateAvailability(1L, "9-5");

        verify(userRepository).save(user);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}
