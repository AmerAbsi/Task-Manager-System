package com.example.taskmanager.model.mapper.impl;

import com.example.taskmanager.model.dto.response.CommentResponseDTO;
import com.example.taskmanager.model.entity.Comment;
import com.example.taskmanager.model.entity.User;
import com.example.taskmanager.model.mapper.CommentMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentResponseDTO toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        User author = comment.getAuthor();
        Comment parent = comment.getParent();

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .parentId(parent != null ? parent.getId() : null)
                .createdAt(comment.getCreatedAt())
                .replies(new ArrayList<>())
                .build();
    }
}