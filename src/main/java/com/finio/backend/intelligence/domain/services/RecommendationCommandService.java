package com.finio.backend.intelligence.domain.services;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationCommand;
import com.finio.backend.intelligence.domain.model.commands.DeleteRecommendationCommand;
import java.util.Optional;

public interface RecommendationCommandService {
    Optional<Recommendation> handle(CreateRecommendationCommand command);
    boolean handle(DeleteRecommendationCommand command);
}
