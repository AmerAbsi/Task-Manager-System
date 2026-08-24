package com.example.taskmanager.model.dto.response;

import com.example.taskmanager.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private NotificationType type;
    private String message;
    private boolean read;
    private Long taskId;
    private String taskTitle;
    private LocalDateTime createdAt;
}