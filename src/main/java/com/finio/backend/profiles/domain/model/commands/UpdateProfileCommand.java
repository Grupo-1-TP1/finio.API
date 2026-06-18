package com.finio.backend.profiles.domain.model.commands;

import java.math.BigDecimal;

public record UpdateProfileCommand(Long userId, String name, BigDecimal saving_percentage, Boolean allow_ml_analysis, Boolean allow_push_notifications, Boolean use_biometrics) {
}
