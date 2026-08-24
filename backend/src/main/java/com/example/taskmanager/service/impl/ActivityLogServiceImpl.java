package com.example.taskmanager.service.impl;

import com.example.taskmanager.model.dto.response.ActivityLogResponseDTO;
import com.example.taskmanager.model.entity.ActivityLog;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.model.mapper.ActivityLogMapper;
import com.example.taskmanager.repository.ActivityLogRepository;
import com.example.taskmanager.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    @Override
    @Transactional
    public void log(ActionType action, String details, User user) {

        ActivityLog entry = ActivityLog.builder()
                .action(action)
                .details(truncate(details))
                .user(user)
                .username(user.getUsername())
                .build();

        activityLogRepository.save(entry);
    }

    @Override
    @Transactional
    public void log(ActionType action, String details, String username) {

        ActivityLog entry = ActivityLog.builder()
                .action(action)
                .details(truncate(details))
                .username(username)
                .build();

        activityLogRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponseDTO> searchLogs(ActionType action,
                                                   String username,
                                                   Pageable pageable) {

        String normalizedUsername =
                (username == null || username.isBlank()) ? null : username.trim();

        Page<ActivityLog> logs =
                activityLogRepository.searchLogs(action, normalizedUsername, pageable);

        return logs.map(activityLogMapper::toResponse);
    }

    private String truncate(String details) {
        if (details == null) {
            return "";
        }
        return details.length() > 500 ? details.substring(0, 500) : details;
    }
}