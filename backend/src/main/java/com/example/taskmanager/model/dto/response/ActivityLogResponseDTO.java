package com.example.taskmanager.model.dto.response;

import com.example.taskmanager.model.enums.ActionType;
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
public class ActivityLogResponseDTO {

    private Long id;
    private ActionType action;
    private String details;
    private Long userId;
    private String username;
    private String fullName;
    private LocalDateTime createdAt;
}