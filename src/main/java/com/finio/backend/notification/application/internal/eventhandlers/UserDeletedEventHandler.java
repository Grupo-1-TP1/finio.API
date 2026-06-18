package com.finio.backend.notification.application.internal.eventhandlers;

import com.finio.backend.iam.domain.model.events.UserAccountDeletedEvent;
import com.finio.backend.notification.infrastructure.persistence.jpa.NotificationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDeletedEventHandler {
    private final NotificationRepository notificationRepository;

    public UserDeletedEventHandler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    @Transactional
    public void on(UserAccountDeletedEvent event) {
        Long userId = event.userId();

        notificationRepository.deleteByUserId(userId);
        System.out.println(userId + " notifications deleted");
    }
}
