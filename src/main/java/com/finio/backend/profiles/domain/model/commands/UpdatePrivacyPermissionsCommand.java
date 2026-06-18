package com.finio.backend.profiles.domain.model.commands;

public record UpdatePrivacyPermissionsCommand(
        Long userId,
        Boolean allow_ml_analysis,
        Boolean allow_push_notifications,
        Boolean use_biometrics
) {}