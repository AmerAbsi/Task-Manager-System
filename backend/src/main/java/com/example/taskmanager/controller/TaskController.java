package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.request.TaskRequestDTO;
import com.example.taskmanager.model.dto.request.TaskStatusUpdateRequestDTO;
import com.example.taskmanager.model.dto.request.TaskUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.TaskResponseDTO;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO created = taskService.createTask(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequestDTO request) {

        TaskResponseDTO updated = taskService.updateTask(id, request);

        return ResponseEntity.ok(updated);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {

        TaskResponseDTO task = taskService.getTaskById(id, authentication.getName());

        return ResponseEntity.ok(task);
    }



    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> searchTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedUserId,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable,
            Authentication authentication) {

        Page<TaskResponseDTO> tasks = taskService.searchTasks(
                search, status, assignedUserId, authentication.getName(), pageable);

        return ResponseEntity.ok(tasks);
    }



    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequestDTO request,
            Authentication authentication) {

        TaskResponseDTO updated = taskService.updateTaskStatus(
                id, request, authentication.getName());

        return ResponseEntity.ok(updated);
    }


}