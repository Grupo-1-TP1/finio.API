package com.finio.backend.notification.application.internal.commandservices;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.domain.model.commands.CreateNotificationCommand;
import com.finio.backend.notification.domain.model.commands.DeleteNotificationCommand;
import com.finio.backend.notification.domain.services.NotificationCommandService;
import com.finio.backend.notification.infrastructure.persistence.jpa.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Notification> handle(CreateNotificationCommand command) {
        try {
            Notification notification = new Notification(command);
            return Optional.of(notificationRepository.save(notification));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteNotificationCommand command) {
        if (!notificationRepository.existsById(command.notificationId())) {
            return false;
        }
        try {
            notificationRepository.deleteById(command.notificationId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
