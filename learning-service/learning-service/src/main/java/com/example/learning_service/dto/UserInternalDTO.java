package com.example.learning_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalDTO {

    private Long userId;
    private String role;
    private Boolean isMentorApproved;
}
