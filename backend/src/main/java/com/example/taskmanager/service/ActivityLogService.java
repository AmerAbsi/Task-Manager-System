package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.response.ActivityLogResponseDTO;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {

    void log(ActionType action, String details, User user);

    void log(ActionType action, String details, String username);

    Page<ActivityLogResponseDTO> searchLogs(ActionType action, String username, Pageable pageable);
}