package com.example.learning_service.service;

import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.SkillRequestDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.Skill;
import com.example.learning_service.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    // CREATE
    public UserResponseDTO createSkill(SkillRequestDTO dto) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("Skill name cannot be empty");
        }


        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("EMAIL: " + email);
        UserInternalDTO user = userClient.getUserByEmail(email);
        System.out.println("USER: " + user);;

        Skill skill = modelMapper.map(dto, Skill.class);
        skill.setUserId(user.getUserId());

        skillRepository.save(skill);

        return new UserResponseDTO("Skill created successfully");
    }

    // UPDATE (ONLY OWNER)
    public UserResponseDTO updateSkill(Long id, SkillRequestDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        Skill skill = skillRepository.findBySkillIdAndUserId(id, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Not authorized or skill not found"));

        skill.setName(dto.getName());
        skillRepository.save(skill);

        return new UserResponseDTO("Skill updated successfully");
    }

    // DELETE (OWNER OR ADMIN)
    public UserResponseDTO deleteSkill(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getUserId().equals(user.getUserId()) &&
                !"ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Not authorized");
        }

        skillRepository.delete(skill);

        return new UserResponseDTO("Skill deleted successfully");
    }

    // GET MY SKILLS
    public List<Skill> getMySkills() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        return skillRepository.findByUserId(user.getUserId());
    }

    // GET BY USER (PUBLIC)
    public List<Skill> getSkillsByUser(Long userId) {
        return skillRepository.findByUserId(userId);
    }
}