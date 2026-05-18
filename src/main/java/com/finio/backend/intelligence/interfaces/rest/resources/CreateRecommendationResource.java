package com.finio.backend.intelligence.interfaces.rest.resources;

import java.util.List;

public record CreateRecommendationResource(
        Long userId,
        Double projectedSavings,
        List<CreateRecommendationDetailResource> details
) {}
