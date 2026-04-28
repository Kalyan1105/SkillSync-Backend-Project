package com.example.user_service.controller;

import com.example.user_service.dto.UpdateProfileDTO;
import com.example.user_service.dto.UserInternalDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileDTO request) {

        userService.updateUser(id, request);
        return new UserResponseDTO("Profile updated successfully");
    }



    @GetMapping("/internal/{id}")
    public UserInternalDTO getUserInternal(@PathVariable Long id) {

        User user = userService.getUserEntityById(id);

        return new UserInternalDTO(
                user.getUserId(),
                user.getRole(),
                user.getIsMentorApproved()
        );
    }

    @GetMapping("/internal/email/{email}")
    public UserInternalDTO getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        return new UserInternalDTO(
                user.getUserId(),
                user.getRole(),
                user.getIsMentorApproved()
        );
    }
}