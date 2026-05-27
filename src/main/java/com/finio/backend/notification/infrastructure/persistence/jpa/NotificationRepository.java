package com.finio.backend.notification.infrastructure.persistence.jpa;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Recupera todas las alertas de un estudiante específico para su bandeja de entrada
    List<Notification> findByUserId(Long userId);
}
