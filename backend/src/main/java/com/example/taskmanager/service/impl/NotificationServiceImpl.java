package com.example.taskmanager.service.impl;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.dto.response.NotificationResponseDTO;
import com.example.taskmanager.model.entity.Notification;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.NotificationType;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.model.mapper.NotificationMapper;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFICATION_DESTINATION = "/queue/notifications";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;
    @Override
    @Transactional
    public void notifyUser(User recipient, NotificationType type, String message, Task task) {

        if (recipient == null) {
            return;
        }

        Notification notification = Notification.builder()
                .type(type)
                .message(truncate(message))
                .read(false)
                .recipient(recipient)
                .task(task)
                .build();

        Notification saved = notificationRepository.save(notification);

        NotificationResponseDTO payload = notificationMapper.toResponse(saved);

        try {
            messagingTemplate.convertAndSendToUser(
                    recipient.getUsername(),
                    NOTIFICATION_DESTINATION,
                    payload);
        } catch (Exception ex) {
            log.warn("Failed to push notification to {}: {}",
                    recipient.getUsername(), ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getMyNotifications(String username, Pageable pageable) {

        User user = loadUser(username);

        return notificationRepository
                .findByRecipientIdWithTask(user.getId(), pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {

        User user = loadUser(username);

        return notificationRepository.countByRecipientIdAndReadFalse(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long id, String username) {

        User user = loadUser(username);

        Notification notification = notificationRepository
                .findByIdAndRecipientId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));

        notification.setRead(true);

        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void notifyAdmins(NotificationType type, String message, Task task, User except) {

        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            if (except != null && admin.getId().equals(except.getId())) {
                continue;
            }
            notifyUser(admin, type, message, task);
        }
    }

    @Override
    @Transactional
    public int markAllAsRead(String username) {

        User user = loadUser(username);

        return notificationRepository.markAllAsRead(user.getId());
    }

    private User loadUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));
    }

    private String truncate(String message) {
        if (message == null) {
            return "";
        }
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}