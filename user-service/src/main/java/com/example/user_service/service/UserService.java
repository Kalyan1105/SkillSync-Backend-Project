
package com.example.user_service.service;



import com.example.user_service.dto.UpdateProfileDTO;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // GET USER
    public Object getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    // GET ALL
    public List<Object> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    public void updateUser(Long id, UpdateProfileDTO request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!loggedInUser.getUserId().equals(id)) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        modelMapper.map(request, user);

        userRepository.save(user);
    }


    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Object mapToDTO(User user) {
        return modelMapper.map(user, UserProfileDTO.class);
    }

}