package com.finio.backend.notification.interfaces.rest.transform;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getNotificationId(), // Recupera el ID heredado de la clase base
                entity.getUserId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType().name(),
                entity.getIsRead()
        );
    }
}
