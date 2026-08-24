package com.example.taskmanager.model.mapper.impl;

import com.example.taskmanager.model.dto.response.ActivityLogResponseDTO;
import com.example.taskmanager.model.entity.ActivityLog;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.mapper.ActivityLogMapper;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapperImpl implements ActivityLogMapper {

    @Override
    public ActivityLogResponseDTO toResponse(ActivityLog activityLog) {
        if (activityLog == null) {
            return null;
        }

        User user = activityLog.getUser();

        return ActivityLogResponseDTO.builder()
                .id(activityLog.getId())
                .action(activityLog.getAction())
                .details(activityLog.getDetails())
                .userId(user != null ? user.getId() : null)
                .username(activityLog.getUsername())
                .fullName(user != null ? user.getFullName() : null)
                .createdAt(activityLog.getCreatedAt())
                .build();
    }
}