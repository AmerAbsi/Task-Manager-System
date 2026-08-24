package com.example.taskmanager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private Long parentId;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<CommentResponseDTO> replies = new ArrayList<>();
}