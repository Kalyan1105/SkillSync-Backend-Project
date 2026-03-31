package com.example.session_service.ServiceTest;

import com.example.session_service.client.UserClient;
import com.example.session_service.dto.*;
import com.example.session_service.entity.*;
import com.example.session_service.repository.SessionRepository;
import com.example.session_service.service.SessionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserClient userClient;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private SessionService sessionService;

    private void mockSecurityContext(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void bookSession_success() {
        mockSecurityContext("test@gmail.com");

        UserInternalDTO learner = new UserInternalDTO(1L, "ROLE_LEARNER", true);
        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        when(userClient.getUserByEmail(any())).thenReturn(learner);
        when(userClient.getUserById(2L)).thenReturn(mentor);

        SessionRequestDTO request = new SessionRequestDTO();
        request.setMentorId(2L);
        request.setTopic("Java");
        request.setSessionTime(LocalDateTime.now());

        SessionResponseDTO response = sessionService.bookSession(request);

        assertEquals("Session booked successfully", response.getMessage());
    }

    @Test
    void bookSession_invalidMentor() {
        mockSecurityContext("test@gmail.com");

        UserInternalDTO learner = new UserInternalDTO(1L, "ROLE_LEARNER", true);
        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_LEARNER", true);

        when(userClient.getUserByEmail(any())).thenReturn(learner);
        when(userClient.getUserById(2L)).thenReturn(mentor);

        SessionRequestDTO request = new SessionRequestDTO();
        request.setMentorId(2L);
        request.setTopic("Java");
        request.setSessionTime(LocalDateTime.now());

        assertThrows(RuntimeException.class, () -> sessionService.bookSession(request));
    }

    @Test
    void acceptSession_success() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        Session session = Session.builder()
                .sessionId(1L)
                .mentorId(2L)
                .status(SessionStatus.REQUESTED)
                .build();

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        SessionResponseDTO response = sessionService.acceptSession(1L);

        assertEquals("Session accepted successfully", response.getMessage());
    }

    @Test
    void acceptSession_notAuthorized() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(3L, "ROLE_MENTOR", true);

        Session session = Session.builder()
                .sessionId(1L)
                .mentorId(2L)
                .status(SessionStatus.REQUESTED)
                .build();

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(RuntimeException.class, () -> sessionService.acceptSession(1L));
    }

    @Test
    void rejectSession_success() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        Session session = Session.builder()
                .sessionId(1L)
                .mentorId(2L)
                .status(SessionStatus.REQUESTED)
                .build();

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        SessionResponseDTO response = sessionService.rejectSession(1L);

        assertEquals("Session rejected successfully", response.getMessage());
    }
    @Test
    void bookSession_mentorNotApproved() {
        mockSecurityContext("test@gmail.com");

        UserInternalDTO learner = new UserInternalDTO(1L, "ROLE_LEARNER", true);
        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", false);

        when(userClient.getUserByEmail(any())).thenReturn(learner);
        when(userClient.getUserById(2L)).thenReturn(mentor);

        SessionRequestDTO request = new SessionRequestDTO();
        request.setMentorId(2L);
        request.setTopic("Java");
        request.setSessionTime(LocalDateTime.now());

        assertThrows(RuntimeException.class, () -> sessionService.bookSession(request));
    }

    @Test
    void acceptSession_alreadyProcessed() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        Session session = Session.builder()
                .sessionId(1L)
                .mentorId(2L)
                .status(SessionStatus.ACCEPTED)
                .build();

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(RuntimeException.class, () -> sessionService.acceptSession(1L));
    }

    @Test
    void rejectSession_alreadyProcessed() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        Session session = Session.builder()
                .sessionId(1L)
                .mentorId(2L)
                .status(SessionStatus.REJECTED)
                .build();

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(RuntimeException.class, () -> sessionService.rejectSession(1L));
    }

    @Test
    void acceptSession_notFound() {
        mockSecurityContext("mentor@gmail.com");

        UserInternalDTO mentor = new UserInternalDTO(2L, "ROLE_MENTOR", true);

        when(userClient.getUserByEmail(any())).thenReturn(mentor);
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> sessionService.acceptSession(1L));
    }

    @Test
    void getByLearner_success() {
        mockSecurityContext("learner@gmail.com");
        UserInternalDTO learner = new UserInternalDTO(1L, "ROLE_LEARNER", true);
        when(userClient.getUserByEmail(any())).thenReturn(learner);
        when(sessionRepository.findByLearnerId(1L)).thenReturn(List.of(new Session()));

        List<Session> sessions = sessionService.getByLearner();

        assertNotNull(sessions);
        assertEquals(1, sessions.size());
    }
    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}