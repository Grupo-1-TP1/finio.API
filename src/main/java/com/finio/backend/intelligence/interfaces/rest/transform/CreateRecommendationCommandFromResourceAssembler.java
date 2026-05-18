package com.finio.backend.intelligence.interfaces.rest.transform;

import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationCommand;
import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationDetailCommand;
import com.finio.backend.intelligence.interfaces.rest.resources.CreateRecommendationResource;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CreateRecommendationCommandFromResourceAssembler {
    public static CreateRecommendationCommand toCommandFromResource(CreateRecommendationResource resource) {
        var detailCommands = resource.details() == null ? new ArrayList<CreateRecommendationDetailCommand>() :
                resource.details().stream().map(detail -> new CreateRecommendationDetailCommand(
                        detail.categoryId(),
                        detail.currentLimit(),
                        detail.suggestedLimit()
                )).collect(Collectors.toList());

        return new CreateRecommendationCommand(
                resource.userId(),
                resource.projectedSavings(),
                detailCommands
        );
    }
}
