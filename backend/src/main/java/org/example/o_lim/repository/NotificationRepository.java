package org.example.o_lim.repository;

import org.example.o_lim.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository <Notification, Long> {
    Long id(Long id);

    @Query("""
        SELECT n FROM Notification n WHERE n.project.id = :projectId
    """)
    List<Notification> findAllNotificationById(Long projectId);

    Optional<Notification> findByIdAndProjectId(Long notificationId, Long projectId);
}
