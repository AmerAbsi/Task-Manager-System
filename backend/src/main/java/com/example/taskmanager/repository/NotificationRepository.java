package com.example.taskmanager.repository;

import com.example.taskmanager.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT n FROM Notification n
            LEFT JOIN FETCH n.task
            WHERE n.recipient.id = :recipientId
            ORDER BY n.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(n) FROM Notification n
            WHERE n.recipient.id = :recipientId
            """)
    Page<Notification> findByRecipientIdWithTask(@Param("recipientId") Long recipientId,
                                                 Pageable pageable);

    long countByRecipientIdAndReadFalse(Long recipientId);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :recipientId AND n.read = false")
    int markAllAsRead(@Param("recipientId") Long recipientId);
}