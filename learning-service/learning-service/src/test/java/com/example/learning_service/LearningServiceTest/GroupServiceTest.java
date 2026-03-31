package com.example.learning_service.LearningServiceTest;

import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.GroupDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.entity.LearningGroup;
import com.example.learning_service.repository.GroupMemberRepository;
import com.example.learning_service.repository.GroupRepository;
import com.example.learning_service.service.GroupService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @Mock
    private UserClient userClient;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private GroupService groupService;

    private void mockSecurity(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createGroup_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "ROLE_USER", true));

        GroupDTO dto = new GroupDTO(null, "Java", "desc", null);

        groupService.createGroup(dto);

        verify(groupRepository).save(any(LearningGroup.class));
    }

    @Test
    void joinGroup_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));
        when(groupRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsByGroupIdAndUserId(1L, 1L)).thenReturn(false);

        groupService.joinGroup(1L);

        verify(memberRepository).save(any());
    }

    @Test
    void joinGroup_alreadyJoined() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));
        when(groupRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsByGroupIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> groupService.joinGroup(1L));
    }

    @Test
    void updateGroup_unauthorized() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(2L, "", true));

        LearningGroup group = new LearningGroup(1L, "Java", "", 1L);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        assertThrows(RuntimeException.class,
                () -> groupService.updateGroup(1L, new GroupDTO()));
    }

    @Test
    void leaveGroup_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L, "", true));

        var member = new com.example.learning_service.entity.GroupMember(1L,1L,1L);

        when(memberRepository.findByGroupIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(member));

        groupService.leaveGroup(1L);

        verify(memberRepository).delete(member);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}
