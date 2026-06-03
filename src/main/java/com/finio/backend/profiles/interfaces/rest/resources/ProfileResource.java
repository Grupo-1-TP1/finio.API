package com.finio.backend.profiles.interfaces.rest.resources;

import java.math.BigDecimal;

public record ProfileResource(Long id, String name, Long userId, BigDecimal saving_percentage, Boolean allow_ml_analysis, Boolean allow_push_notifications, Boolean use_biometrics) {}