package com.finio.backend.profiles.interfaces.rest.resources;

import java.math.BigDecimal;

public record UpdateProfileResource(String name, BigDecimal saving_percentage, Boolean use_ml_analysis, Boolean allow_push_notifications, Boolean use_biometrics) {
}
