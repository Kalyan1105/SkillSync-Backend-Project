package com.example.session_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionRequestDTO {

    @NotNull
    private Long mentorId;

    @NotBlank
    private String topic;

    @NotNull
    private LocalDateTime sessionTime;
}