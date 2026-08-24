package com.example.taskmanager.repository;

import com.example.taskmanager.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.task.id = :taskId
            ORDER BY c.createdAt ASC
            """)
    List<Comment> findByTaskIdWithAuthor(@Param("taskId") Long taskId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.id = :id
            """)
    Optional<Comment> findByIdWithAuthor(@Param("id") Long id);

    long countByTaskId(Long taskId);
}