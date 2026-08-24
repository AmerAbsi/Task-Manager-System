package com.example.taskmanager.model.mapper.impl;

import com.example.taskmanager.model.dto.response.NotificationResponseDTO;
import com.example.taskmanager.model.entity.Notification;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.mapper.NotificationMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponseDTO toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        Task task = notification.getTask();

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .taskId(task != null ? task.getId() : null)
                .taskTitle(task != null ? task.getTitle() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}