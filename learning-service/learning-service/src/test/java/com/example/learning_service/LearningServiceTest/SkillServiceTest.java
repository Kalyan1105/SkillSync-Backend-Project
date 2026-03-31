package com.example.learning_service.LearningServiceTest;


import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.SkillRequestDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.entity.Skill;
import com.example.learning_service.repository.SkillRepository;
import com.example.learning_service.service.SkillService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserClient userClient;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private SkillService skillService;

    private void mockSecurity(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createSkill_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));

        SkillRequestDTO dto = new SkillRequestDTO(null, "Java");

        skillService.createSkill(dto);

        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void createSkill_invalid() {
        //mockSecurity("a@gmail.com");

        SkillRequestDTO dto = new SkillRequestDTO(null, "");

        assertThrows(RuntimeException.class, () -> skillService.createSkill(dto));
    }

    @Test
    void updateSkill_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));

        Skill skill = new Skill(1L,1L,"Java");

        when(skillRepository.findBySkillIdAndUserId(1L,1L))
                .thenReturn(Optional.of(skill));

        skillService.updateSkill(1L, new SkillRequestDTO(null,"Python"));

        verify(skillRepository).save(skill);
    }

    @Test
    void deleteSkill_unauthorized() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(2L, "ROLE_USER", true));

        Skill skill = new Skill(1L,1L,"Java");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        assertThrows(RuntimeException.class, () -> skillService.deleteSkill(1L));
    }

    @Test
    void getMySkills_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));

        when(skillRepository.findByUserId(1L)).thenReturn(java.util.List.of(new Skill()));

        assertEquals(1, skillService.getMySkills().size());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}