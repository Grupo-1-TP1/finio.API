package com.finio.backend.notification.interfaces.rest.resources;

public record NotificationResource(
        Long id,
        Long userId,
        String title,
        String message,
        String type,
        Boolean isRead
) {}
