package com.finio.backend.notification.interfaces.rest.transform;

import com.finio.backend.notification.domain.model.aggregates.NotificationType;
import com.finio.backend.notification.domain.model.commands.CreateNotificationCommand;
import com.finio.backend.notification.interfaces.rest.resources.CreateNotificationResource;

public class CreateNotificationCommandFromResourceAssembler {
    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        return new CreateNotificationCommand(
                resource.userId(),
                resource.title(),
                resource.message(),
                NotificationType.valueOf(resource.type().toUpperCase()) // Parsea el String al Enum de dominio
        );
    }
}
