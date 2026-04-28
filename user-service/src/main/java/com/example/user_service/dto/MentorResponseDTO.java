package com.example.user_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String skills;
    private Integer experience;
    private Boolean isMentorApproved;
    private String availability;
}
