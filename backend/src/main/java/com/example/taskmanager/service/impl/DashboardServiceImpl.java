package com.example.taskmanager.service.impl;

import com.example.taskmanager.model.dto.response.DashboardStatsResponseDTO;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponseDTO getStats() {

        return DashboardStatsResponseDTO.builder()
                .totalUsers(userRepository.count())
                .totalTasks(taskRepository.count())
                .pendingTasks(taskRepository.countByStatus(TaskStatus.PENDING))
                .inProgressTasks(taskRepository.countByStatus(TaskStatus.IN_PROGRESS))
                .completedTasks(taskRepository.countByStatus(TaskStatus.COMPLETED))
                .build();
    }
}