
package com.example.session_service.service;

import com.example.session_service.client.UserClient;
import com.example.session_service.dto.*;
import com.example.session_service.entity.*;
import com.example.session_service.exception.InvalidRequestException;
import com.example.session_service.exception.ResourceNotFoundException;
import com.example.session_service.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    // BOOK SESSION
    public SessionResponseDTO bookSession(SessionRequestDTO request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO learner = userClient.getUserByEmail(email);

        UserInternalDTO mentor = userClient.getUserById(request.getMentorId());

        if (!"ROLE_MENTOR".equals(mentor.getRole())) {
            throw new InvalidRequestException("Invalid mentor");
        }

        if (!Boolean.TRUE.equals(mentor.getIsMentorApproved())) {
            throw new InvalidRequestException("Mentor not approved");
        }

        Session session = modelMapper.map(request, Session.class);
        session.setLearnerId(learner.getUserId());
        session.setStatus(SessionStatus.REQUESTED);

        sessionRepository.save(session);

        return new SessionResponseDTO("Session booked successfully");
    }

    public SessionResponseDTO acceptSession(Long sessionId) {

        // 🔥 extract mentor from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserInternalDTO mentor = userClient.getUserByEmail(email);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getMentorId().equals(mentor.getUserId())) {
            throw new InvalidRequestException("Not authorized");
        }

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new InvalidRequestException("Session already processed");
        }

        session.setStatus(SessionStatus.ACCEPTED);
        sessionRepository.save(session);

        return new SessionResponseDTO("Session accepted successfully");
    }

    public SessionResponseDTO rejectSession(Long sessionId) {

        // 🔐 get logged-in mentor from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserInternalDTO mentor = userClient.getUserByEmail(email);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getMentorId().equals(mentor.getUserId())) {
            throw new InvalidRequestException("Not authorized");
        }

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new InvalidRequestException("Session already processed");
        }

        session.setStatus(SessionStatus.REJECTED);
        sessionRepository.save(session);

        return new SessionResponseDTO("Session rejected successfully");
    }

    // GET
    public List<Session> getByLearner() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO learner = userClient.getUserByEmail(email);
        return sessionRepository.findByLearnerId(learner.getUserId());
    }

    public List<Session> getByMentor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO mentor = userClient.getUserByEmail(email);
        return sessionRepository.findByMentorId(mentor.getUserId());
    }


}