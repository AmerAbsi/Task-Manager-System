package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.request.TaskRequestDTO;
import com.example.taskmanager.model.dto.request.TaskStatusUpdateRequestDTO;
import com.example.taskmanager.model.dto.request.TaskUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.TaskResponseDTO;
import com.example.taskmanager.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO request);

    TaskResponseDTO getTaskById(Long id, String currentUsername);

    Page<TaskResponseDTO> searchTasks(String search,
                                      TaskStatus status,
                                      Long assignedUserId,
                                      String currentUsername,
                                      Pageable pageable);

    TaskResponseDTO updateTaskStatus(Long id,
                                     TaskStatusUpdateRequestDTO request,
                                     String currentUsername);

    void deleteTask(Long id);
}