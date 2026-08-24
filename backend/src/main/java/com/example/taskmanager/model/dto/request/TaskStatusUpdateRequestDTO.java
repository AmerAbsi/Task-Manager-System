package com.example.taskmanager.model.dto.request;

import com.example.taskmanager.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private TaskStatus status;
}