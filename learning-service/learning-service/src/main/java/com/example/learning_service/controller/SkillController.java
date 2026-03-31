package com.example.learning_service.controller;

import com.example.learning_service.dto.SkillRequestDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.Skill;
import com.example.learning_service.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // CREATE
    @PostMapping
    public UserResponseDTO create(@RequestBody SkillRequestDTO dto) {
        return skillService.createSkill(dto);
    }

    // UPDATE
    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @RequestBody SkillRequestDTO dto) {
        return skillService.updateSkill(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public UserResponseDTO delete(@PathVariable Long id) {
        return skillService.deleteSkill(id);
    }

    // GET SKILLS
    @GetMapping
    public List<Skill> mySkills() {
        return skillService.getMySkills();
    }

    // GET SKILLS BY USER
    @GetMapping("/user/{userId}")
    public List<Skill> getByUser(@PathVariable Long userId) {
        return skillService.getSkillsByUser(userId);
    }
}