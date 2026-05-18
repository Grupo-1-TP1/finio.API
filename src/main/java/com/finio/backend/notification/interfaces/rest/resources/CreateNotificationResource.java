package com.finio.backend.notification.interfaces.rest.resources;

public record CreateNotificationResource(
        Long userId,
        String title,
        String message,
        String type // Se recibe como String ("BUDGET_ALERT", "SYSTEM", etc.)
) {}
