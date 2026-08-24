package com.example.taskmanager.service.impl;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.dto.request.CommentRequestDTO;
import com.example.taskmanager.model.dto.response.CommentResponseDTO;
import com.example.taskmanager.model.entity.Comment;
import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.enums.ActionType;
import com.example.taskmanager.model.enums.NotificationType;
import com.example.taskmanager.model.enums.Role;
import com.example.taskmanager.model.mapper.CommentMapper;
import com.example.taskmanager.repository.CommentRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.ActivityLogService;
import com.example.taskmanager.service.CommentService;
import com.example.taskmanager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    @Override
    @Transactional
    public CommentResponseDTO addComment(Long taskId,
                                         CommentRequestDTO request,
                                         String currentUsername) {

        Task task = loadTaskWithAccessCheck(taskId, currentUsername);

        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + currentUsername));

        Comment parent = null;

        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent comment not found with id: " + request.getParentId()));

            if (!parent.getTask().getId().equals(taskId)) {
                throw new AccessDeniedException(
                        "Parent comment does not belong to this task");
            }
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .parent(parent)
                .build();

        Comment saved = commentRepository.save(comment);

        activityLogService.log(ActionType.COMMENT_ADDED,
                "Commented on task '" + task.getTitle() + "'",
                author);


        String commentMessage = author.getFullName()
                + " commented on task '" + task.getTitle() + "'";

        User assignee = task.getAssignedUser();

        if (assignee != null && !assignee.getId().equals(author.getId())) {
            notificationService.notifyUser(
                    assignee, NotificationType.COMMENT_ADDED, commentMessage, task);
        }

        notificationService.notifyAdmins(NotificationType.COMMENT_ADDED, commentMessage, task, author);

        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTask(Long taskId, String currentUsername) {

        loadTaskWithAccessCheck(taskId, currentUsername);

        List<Comment> flatComments = commentRepository.findByTaskIdWithAuthor(taskId);

        return buildTree(flatComments);
    }




    private Task loadTaskWithAccessCheck(Long taskId, String username) {

        Task task = taskRepository.findByIdWithUser(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        if (currentUser.getRole() != Role.ADMIN) {
            User assignedUser = task.getAssignedUser();

            if (assignedUser == null || !assignedUser.getId().equals(currentUser.getId())) {
                throw new AccessDeniedException(
                        "You do not have permission to access this task");
            }
        }

        return task;
    }


    private List<CommentResponseDTO> buildTree(List<Comment> flatComments) {

        Map<Long, CommentResponseDTO> byId = new LinkedHashMap<>();

        for (Comment comment : flatComments) {
            byId.put(comment.getId(), commentMapper.toResponse(comment));
        }

        List<CommentResponseDTO> roots = new ArrayList<>();

        for (Comment comment : flatComments) {

            CommentResponseDTO dto = byId.get(comment.getId());
            Comment parent = comment.getParent();

            if (parent == null) {
                roots.add(dto);
            } else {
                CommentResponseDTO parentDto = byId.get(parent.getId());

                if (parentDto != null) {
                    parentDto.getReplies().add(dto);
                } else {
                    roots.add(dto);
                }
            }
        }

        return roots;
    }
}