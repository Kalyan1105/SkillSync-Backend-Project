package com.example.learning_service.repository;

import com.example.learning_service.entity.LearningGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<LearningGroup, Long> {
}
