package com.example.taskmanager.model.mapper;

import com.example.taskmanager.model.dto.response.NotificationResponseDTO;
import com.example.taskmanager.model.entity.Notification;

public interface NotificationMapper {
    NotificationResponseDTO toResponse(Notification notification);
}