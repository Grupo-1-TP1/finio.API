package com.finio.backend.intelligence.domain.model.commands;

public record CreateRecommendationDetailCommand(
        Long categoryId,
        Double currentLimit,
        Double suggestedLimit
) {}
