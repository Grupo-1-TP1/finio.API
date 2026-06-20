package com.finio.backend.intelligence.interfaces.rest.transform;

import com.finio.backend.intelligence.domain.model.commands.CreatePredictionCommand;
import com.finio.backend.intelligence.interfaces.rest.resources.CreatePredictionResource;

public class CreatePredictionCommandFromResourceAssembler {
    public static CreatePredictionCommand toCommandFromResource(CreatePredictionResource resource) {
        return new CreatePredictionCommand(
                resource.confidenceScore(),
                resource.categoryId(),
                resource.text(),
                resource.transactionId()
        );
    }
}
