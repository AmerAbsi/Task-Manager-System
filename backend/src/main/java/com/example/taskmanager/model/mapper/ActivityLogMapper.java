package com.example.taskmanager.model.mapper;

import com.example.taskmanager.model.dto.response.ActivityLogResponseDTO;
import com.example.taskmanager.model.entity.ActivityLog;

public interface ActivityLogMapper {
    ActivityLogResponseDTO toResponse(ActivityLog activityLog);
}