package com.finio.backend.intelligence.domain.model.commands;

import java.util.List;

public record CreateRecommendationCommand(
        Long userId,
        Double projectedSavings,
        List<CreateRecommendationDetailCommand> details
) {}
