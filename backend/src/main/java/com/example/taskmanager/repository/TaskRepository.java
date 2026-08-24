package com.example.taskmanager.repository;

import com.example.taskmanager.model.entity.Task;
import com.example.taskmanager.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
            SELECT t FROM Task t
            LEFT JOIN FETCH t.assignedUser
            WHERE (:search IS NULL
                   OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR t.status = :status)
              AND (:assignedUserId IS NULL
                   OR (:assignedUserId = -1 AND t.assignedUser IS NULL)
                   OR t.assignedUser.id = :assignedUserId)
            """,
            countQuery = """
            SELECT COUNT(t) FROM Task t
            WHERE (:search IS NULL
                   OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR t.status = :status)
              AND (:assignedUserId IS NULL
                   OR (:assignedUserId = -1 AND t.assignedUser IS NULL)
                   OR t.assignedUser.id = :assignedUserId)
            """)
    Page<Task> searchTasks(@Param("search") String search,
                           @Param("status") TaskStatus status,
                           @Param("assignedUserId") Long assignedUserId,
                           Pageable pageable);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignedUser WHERE t.id = :id")
    Optional<Task> findByIdWithUser(@Param("id") Long id);

    long countByStatus(TaskStatus status);

    boolean existsByIdAndAssignedUserId(Long id, Long userId);
}