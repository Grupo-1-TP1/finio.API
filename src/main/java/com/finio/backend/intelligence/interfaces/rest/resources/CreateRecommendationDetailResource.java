package com.finio.backend.intelligence.interfaces.rest.resources;

public record CreateRecommendationDetailResource(
        Long categoryId,
        Double currentLimit,
        Double suggestedLimit
) {}
