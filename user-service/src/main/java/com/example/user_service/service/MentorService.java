package com.example.user_service.service;


import com.example.user_service.dto.MentorResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MentorService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @PreAuthorize("hasRole('LEARNER')")
    public void applyForMentor() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole("ROLE_MENTOR");
        user.setIsMentorApproved(false);

        userRepository.save(user);
    }

    // APPROVE (NO RETURN)
    @PreAuthorize("hasRole('ADMIN')")
    public void approveMentor(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsMentorApproved(true);

        userRepository.save(user);
    }

    // UPDATE AVAILABILITY (NO RETURN)
    @PreAuthorize("hasRole('MENTOR')")
    public void updateAvailability(Long id, String availability) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getUserId().equals(id)) {
            throw new RuntimeException("Unauthorized");
        }

        user.setAvailability(availability);

        userRepository.save(user);
    }



    public List<MentorResponseDTO> getMentors() {

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        "ROLE_MENTOR".equals(user.getRole()) &&
                                Boolean.TRUE.equals(user.getIsMentorApproved())
                )
                .map(this::mapToDTO)
                .toList();
    }

    public MentorResponseDTO getMentorById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ROLE_MENTOR".equals(user.getRole())) {
            throw new RuntimeException("Not a mentor");
        }

        if (!Boolean.TRUE.equals(user.getIsMentorApproved())) {
            throw new RuntimeException("Mentor not approved");
        }

        return mapToDTO(user);
    }

    private MentorResponseDTO mapToDTO(User user) {
        return modelMapper.map(user, MentorResponseDTO.class);
    }
}