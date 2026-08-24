package com.example.taskmanager.repository;

import com.example.taskmanager.model.entity.ActivityLog;
import com.example.taskmanager.model.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query(value = """
            SELECT a FROM ActivityLog a
            LEFT JOIN FETCH a.user
            WHERE (:action IS NULL OR a.action = :action)
              AND (:username IS NULL
                   OR LOWER(a.username) LIKE LOWER(CONCAT('%', :username, '%')))
            """,
            countQuery = """
            SELECT COUNT(a) FROM ActivityLog a
            WHERE (:action IS NULL OR a.action = :action)
              AND (:username IS NULL
                   OR LOWER(a.username) LIKE LOWER(CONCAT('%', :username, '%')))
            """)
    Page<ActivityLog> searchLogs(@Param("action") ActionType action,
                                 @Param("username") String username,
                                 Pageable pageable);
}