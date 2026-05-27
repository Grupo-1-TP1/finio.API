package com.finio.backend.intelligence.interfaces.rest.resources;

public record RecommendationDetailResource(
        Long id,
        Long categoryId,
        Double currentLimit,
        Double suggestedLimit
) {}
