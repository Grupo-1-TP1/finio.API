package com.finio.backend.intelligence.interfaces.rest.transform;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import com.finio.backend.intelligence.interfaces.rest.resources.RecommendationDetailResource;
import com.finio.backend.intelligence.interfaces.rest.resources.RecommendationResource;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RecommendationResourceFromEntityAssembler {
    public static RecommendationResource toResourceFromEntity(Recommendation entity) {
        var detailResources = entity.getDetails() == null ? new ArrayList<RecommendationDetailResource>() :
                entity.getDetails().stream().map(detail -> new RecommendationDetailResource(
                        detail.getId(),
                        detail.getCategoryId(),
                        detail.getCurrentLimit(),
                        detail.getSuggestedLimit()
                )).collect(Collectors.toList());

        return new RecommendationResource(
                entity.getRecommendationId(),
                entity.getUserId(),
                entity.getProjectedSavings(),
                entity.getStatus().name(),
                detailResources
        );
    }
}
