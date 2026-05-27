package com.finio.backend.intelligence.interfaces.rest.resources;

import java.util.List;

public record RecommendationResource(
        Long id,
        Long userId,
        Double projectedSavings,
        String status,
        List<RecommendationDetailResource> details
) {}
