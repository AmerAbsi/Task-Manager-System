package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.response.NotificationResponseDTO;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void notifyUser(User recipient, NotificationType type, String message, Task task);

    Page<NotificationResponseDTO> getMyNotifications(String username, Pageable pageable);

    long getUnreadCount(String username);

    NotificationResponseDTO markAsRead(Long id, String username);

    int markAllAsRead(String username);

    void notifyAdmins(NotificationType type, String message, Task task, User except);
}