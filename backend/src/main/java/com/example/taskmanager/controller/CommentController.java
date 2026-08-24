package com.example.taskmanager.controller;

import com.example.taskmanager.model.dto.request.CommentRequestDTO;
import com.example.taskmanager.model.dto.response.CommentResponseDTO;
import com.example.taskmanager.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequestDTO request,
            Authentication authentication) {

        CommentResponseDTO created = commentService.addComment(
                taskId, request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable Long taskId,
            Authentication authentication) {

        List<CommentResponseDTO> comments = commentService.getCommentsByTask(
                taskId, authentication.getName());

        return ResponseEntity.ok(comments);
    }
}