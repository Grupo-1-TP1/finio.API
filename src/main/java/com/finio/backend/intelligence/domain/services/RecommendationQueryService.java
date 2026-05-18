package com.finio.backend.intelligence.domain.services;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationByIdQuery;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface RecommendationQueryService {
    Optional<Recommendation> handle(GetRecommendationByIdQuery query);
    List<Recommendation> handle(GetRecommendationsByUserIdQuery query);
}
