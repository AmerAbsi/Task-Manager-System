package com.example.taskmanager.model.mapper;

import com.example.taskmanager.model.dto.request.TaskRequestDTO;
import com.example.taskmanager.model.dto.response.TaskResponseDTO;
import com.example.taskmanager.model.entity.Task;

import java.util.List;

public interface TaskMapper {

    Task toEntity(TaskRequestDTO dto);

    TaskResponseDTO toResponse(Task task);

    List<TaskResponseDTO> toResponseList(List<Task> tasks);
}