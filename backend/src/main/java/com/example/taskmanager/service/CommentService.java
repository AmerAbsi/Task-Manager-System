package com.example.taskmanager.service;

import com.example.taskmanager.model.dto.request.CommentRequestDTO;
import com.example.taskmanager.model.dto.response.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO addComment(Long taskId,
                                  CommentRequestDTO request,
                                  String currentUsername);

    List<CommentResponseDTO> getCommentsByTask(Long taskId, String currentUsername);
}