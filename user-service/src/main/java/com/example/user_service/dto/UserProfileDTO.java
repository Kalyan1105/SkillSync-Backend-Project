package com.example.user_service.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Long userId;
    private String name;
    private String email;
    private String role;
    private String skills;
    private Integer experience;
}
