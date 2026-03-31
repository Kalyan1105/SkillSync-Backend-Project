package com.example.learning_service.service;

import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.GroupDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.GroupMember;
import com.example.learning_service.entity.LearningGroup;
import com.example.learning_service.exception.InvalidRequestException;
import com.example.learning_service.exception.ResourceNotFoundException;
import com.example.learning_service.repository.GroupMemberRepository;
import com.example.learning_service.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;


    public void createGroup(GroupDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        LearningGroup group = modelMapper.map(dto, LearningGroup.class);
        group.setCreatedBy(user.getUserId());

        groupRepository.save(group);
    }


    public void joinGroup(Long groupId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found");
        }

        if (memberRepository.existsByGroupIdAndUserId(groupId, user.getUserId())) {
            throw new InvalidRequestException("Already joined this group");
        }

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(user.getUserId());

        memberRepository.save(member);
    }


    public void updateGroup(Long id, GroupDTO dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        LearningGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));


        if (!group.getCreatedBy().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized");
        }

        group.setName(dto.getName());
        groupRepository.save(group);
    }


    public void deleteGroup(Long id) {

        LearningGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        groupRepository.delete(group);
    }


    public List<LearningGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public UserResponseDTO leaveGroup(Long groupId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        GroupMember member = memberRepository
                .findByGroupIdAndUserId(groupId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("You are not part of this group"));

        memberRepository.delete(member);

        return new UserResponseDTO("Left group successfully");
    }


}