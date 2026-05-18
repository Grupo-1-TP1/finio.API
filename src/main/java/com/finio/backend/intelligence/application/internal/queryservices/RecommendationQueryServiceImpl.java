package com.finio.backend.intelligence.application.internal.queryservices;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationByIdQuery;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationsByUserIdQuery;
import com.finio.backend.intelligence.domain.services.RecommendationQueryService;
import com.finio.backend.intelligence.infrastructure.persistence.jpa.RecommendationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationQueryServiceImpl implements RecommendationQueryService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationQueryServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public Optional<Recommendation> handle(GetRecommendationByIdQuery query) {
        return recommendationRepository.findById(query.recommendationId());
    }

    @Override
    public List<Recommendation> handle(GetRecommendationsByUserIdQuery query) {
        return recommendationRepository.findByUserId(query.userId());
    }
}
