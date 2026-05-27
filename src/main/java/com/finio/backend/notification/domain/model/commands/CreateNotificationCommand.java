package com.finio.backend.notification.domain.model.commands;

import com.finio.backend.notification.domain.model.aggregates.NotificationType;

public record CreateNotificationCommand(
        Long userId,
        String title,
        String message,
        NotificationType type
) {}
