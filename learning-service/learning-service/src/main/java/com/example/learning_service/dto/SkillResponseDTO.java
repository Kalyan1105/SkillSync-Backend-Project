package com.example.learning_service.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillResponseDTO {
    private Long skillId;
    private String name;
}