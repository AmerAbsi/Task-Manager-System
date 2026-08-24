package com.example.taskmanager.model.mapper.impl;

import com.example.taskmanager.model.dto.request.TaskRequestDTO;
import com.example.taskmanager.model.dto.response.TaskResponseDTO;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.mapper.TaskMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public Task toEntity(TaskRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .dueDate(dto.getDueDate())
                .build();
    }

    @Override
    public TaskResponseDTO toResponse(Task task) {
        if (task == null) {
            return null;
        }

        User assignedUser = task.getAssignedUser();

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .assignedUserId(assignedUser != null ? assignedUser.getId() : null)
                .assignedUserName(assignedUser != null ? assignedUser.getFullName() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    @Override
    public List<TaskResponseDTO> toResponseList(List<Task> tasks) {
        if (tasks == null) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}