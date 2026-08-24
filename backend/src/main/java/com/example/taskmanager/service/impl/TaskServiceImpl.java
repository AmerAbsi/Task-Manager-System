package com.example.taskmanager.service.impl;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.dto.request.TaskRequestDTO;
import com.example.taskmanager.model.dto.request.TaskStatusUpdateRequestDTO;
import com.example.taskmanager.model.dto.request.TaskUpdateRequestDTO;
import com.example.taskmanager.model.dto.response.TaskResponseDTO;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.model.enums.TaskStatus;
import com.example.taskmanager.model.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.CurrentUserProvider;
import com.example.taskmanager.service.ActivityLogService;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.taskmanager.model.enums.NotificationType;
import com.example.taskmanager.service.NotificationService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;
    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO request) {

        Task task = taskMapper.toEntity(request);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
        }

        Task saved = taskRepository.save(task);

        String assignee = saved.getAssignedUser() != null
                ? saved.getAssignedUser().getFullName()
                : "nobody";

        activityLogService.log(ActionType.TASK_CREATED,
                "Created task '" + saved.getTitle() + "' assigned to " + assignee,
                currentUserProvider.getCurrentUser());

        notificationService.notifyUser(
                saved.getAssignedUser(),
                NotificationType.TASK_ASSIGNED,
                "You have been assigned a new task: '" + saved.getTitle() + "'",
                saved);

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO request) {

        Task task = taskRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));

        Long previousAssigneeId = task.getAssignedUser() != null
                ? task.getAssignedUser().getId()
                : null;

        TaskStatus previousStatus = task.getStatus();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        if (request.getAssignedUserId() == null) {
            task.setAssignedUser(null);
        } else {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
        }

        Task saved = taskRepository.save(task);

        activityLogService.log(ActionType.TASK_UPDATED,
                "Updated task '" + saved.getTitle() + "'",
                currentUserProvider.getCurrentUser());

        User currentUser = currentUserProvider.getCurrentUser();
        User newAssignee = saved.getAssignedUser();
        Long newAssigneeId = newAssignee != null ? newAssignee.getId() : null;

        if (newAssigneeId != null && !newAssigneeId.equals(previousAssigneeId)) {
            notificationService.notifyUser(
                    newAssignee,
                    NotificationType.TASK_ASSIGNED,
                    "You have been assigned a task: '" + saved.getTitle() + "'",
                    saved);
        }

        if (saved.getStatus() != previousStatus) {
            String statusMessage = "Task '" + saved.getTitle() + "' changed from "
                    + previousStatus + " to " + saved.getStatus();

            if (newAssignee != null && !newAssignee.getId().equals(currentUser.getId())) {
                notificationService.notifyUser(
                        newAssignee, NotificationType.TASK_STATUS_CHANGED, statusMessage, saved);
            }

            notificationService.notifyAdmins(
                    NotificationType.TASK_STATUS_CHANGED, statusMessage, saved, currentUser);
        }

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id, String currentUsername) {

        Task task = taskRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));


        User currentUser = getCurrentUser(currentUsername);

        if (!isAdmin(currentUser)) {
            User assignedUser = task.getAssignedUser();

            if (assignedUser == null || !assignedUser.getId().equals(currentUser.getId())) {
                throw new AccessDeniedException(
                        "You do not have permission to view this task");
            }
        }

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> searchTasks(String search,
                                             TaskStatus status,
                                             Long assignedUserId,
                                             String currentUsername,
                                             Pageable pageable) {

        User currentUser = getCurrentUser(currentUsername);

        Long effectiveAssignedUserId = isAdmin(currentUser)
                ? assignedUserId
                : currentUser.getId();

        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<Task> tasks = taskRepository.searchTasks(
                normalizedSearch, status, effectiveAssignedUserId, pageable);

        return tasks.map(taskMapper::toResponse);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTaskStatus(Long id,
                                            TaskStatusUpdateRequestDTO request,
                                            String currentUsername) {

        Task task = taskRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));

        User currentUser = getCurrentUser(currentUsername);

        if (!isAdmin(currentUser)) {
            User assignedUser = task.getAssignedUser();

            if (assignedUser == null || !assignedUser.getId().equals(currentUser.getId())) {
                throw new AccessDeniedException(
                        "You do not have permission to update this task");
            }
        }

        TaskStatus previousStatus = task.getStatus();

        task.setStatus(request.getStatus());

        Task saved = taskRepository.save(task);

        activityLogService.log(ActionType.TASK_STATUS_CHANGED,
                "Changed status of '" + saved.getTitle() + "' from "
                        + previousStatus + " to " + saved.getStatus(),
                currentUser);


        String statusMessage = "Task '" + saved.getTitle() + "' changed from "
                + previousStatus + " to " + saved.getStatus();

        User assignee = saved.getAssignedUser();

        if (assignee != null && !assignee.getId().equals(currentUser.getId())) {
            notificationService.notifyUser(
                    assignee, NotificationType.TASK_STATUS_CHANGED, statusMessage, saved);
        }

        notificationService.notifyAdmins(NotificationType.TASK_STATUS_CHANGED, statusMessage, saved, currentUser);

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));

        task.setDeletedAt(LocalDateTime.now());

        taskRepository.save(task);
        activityLogService.log(ActionType.TASK_DELETED,
                "Deleted task '" + task.getTitle() + "'",
                currentUserProvider.getCurrentUser());
    }




    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }
}