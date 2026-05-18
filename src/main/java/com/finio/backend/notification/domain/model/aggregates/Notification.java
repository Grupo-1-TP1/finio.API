package com.finio.backend.notification.domain.model.aggregates;

import com.finio.backend.notification.domain.model.commands.CreateNotificationCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    public Notification(Long userId, String title, String message, NotificationType type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false; // Toda notificación inicia como No Leída
    }

    public Notification(CreateNotificationCommand command) {
        this.userId = command.userId();
        this.title = command.title();
        this.message = command.message();
        this.type = command.type();
        this.isRead = false;
    }
}
