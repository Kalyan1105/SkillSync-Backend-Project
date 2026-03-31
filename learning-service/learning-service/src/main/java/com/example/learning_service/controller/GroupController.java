package com.example.learning_service.controller;

import com.example.learning_service.dto.GroupDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.LearningGroup;
import com.example.learning_service.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Group;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // CREATE GROUP
    @PostMapping
    public UserResponseDTO create(@RequestBody GroupDTO dto) {
        groupService.createGroup(dto);
        return new UserResponseDTO("Group created successfully");
    }

    // JOIN GROUP
    @PostMapping("/{groupId}/join")
    public UserResponseDTO join(@PathVariable Long groupId) {
        groupService.joinGroup(groupId);
        return new UserResponseDTO("Joined group successfully");
    }

    // UPDATE GROUP
    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @RequestBody GroupDTO dto) {
        groupService.updateGroup(id, dto);
        return new UserResponseDTO("Group updated successfully");
    }

    // DELETE GROUP
    @DeleteMapping("/{id}")
    public UserResponseDTO delete(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return new UserResponseDTO("Group deleted successfully");
    }

    // GET
    @GetMapping
    public List<LearningGroup> getAll() {
        return groupService.getAllGroups();
    }

    @DeleteMapping("/{groupId}/leave")
    public UserResponseDTO leaveGroup(@PathVariable Long groupId) {
        return groupService.leaveGroup(groupId);
    }
}