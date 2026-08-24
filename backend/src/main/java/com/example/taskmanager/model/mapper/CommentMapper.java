package com.example.taskmanager.model.mapper;

import com.example.taskmanager.model.dto.response.CommentResponseDTO;
import com.example.taskmanager.model.entity.Comment;

public interface CommentMapper {
    CommentResponseDTO toResponse(Comment comment);
}