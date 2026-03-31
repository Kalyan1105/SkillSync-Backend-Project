package com.example.user_service.controller;

import com.example.user_service.dto.AvailabilityDTO;
import com.example.user_service.dto.MentorResponseDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    // APPLY
    @PostMapping("/apply")
    public UserResponseDTO apply() {
        mentorService.applyForMentor();
        return new UserResponseDTO("Mentor application submitted");
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<MentorResponseDTO>> getMentors() {
        return ResponseEntity.ok(mentorService.getMentors());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MentorResponseDTO> getMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    // APPROVE
    @PutMapping("/{id}/approve")
    public UserResponseDTO approveMentor(@PathVariable Long id) {
        mentorService.approveMentor(id);
        return new UserResponseDTO("Mentor approved successfully");
    }

    // UPDATE AVAILABILITY
    @PutMapping("/{id}/availability")
    public UserResponseDTO updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityDTO request) {

        mentorService.updateAvailability(id, request.getAvailability());
        return new UserResponseDTO("Availability updated successfully");
    }
}