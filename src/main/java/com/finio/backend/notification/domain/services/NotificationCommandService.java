package com.finio.backend.notification.domain.services;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.domain.model.commands.CreateNotificationCommand;
import com.finio.backend.notification.domain.model.commands.DeleteNotificationCommand;
import java.util.Optional;

public interface NotificationCommandService {
    Optional<Notification> handle(CreateNotificationCommand command);
    boolean handle(DeleteNotificationCommand command);
}
