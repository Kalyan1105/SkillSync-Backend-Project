package com.example.learning_service.repository;

import com.example.learning_service.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUserId(Long userId);

    Optional<Skill> findBySkillIdAndUserId(Long skillId, Long userId);
}
