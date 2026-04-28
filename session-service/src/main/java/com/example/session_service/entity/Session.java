package com.example.session_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    private Long learnerId;
    private Long mentorId;

    private String topic;

    private LocalDateTime sessionTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}
