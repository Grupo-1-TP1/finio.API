package com.finio.backend.intelligence.interfaces.rest.transform;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.interfaces.rest.resources.PredictionResource;

public class PredictionResourceFromEntityAssembler {
    public static PredictionResource toResourceFromEntity(Prediction entity) {
        return new PredictionResource(
                entity.getPredictionId(),
                entity.getConfidenceScore(),
                entity.getCategoryId(),
                entity.getText(),
                entity.getTransactionId()
        );
    }
}
