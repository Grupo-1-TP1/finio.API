package com.finio.backend.intelligence.domain.services;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.domain.model.queries.GetPredictionByIdQuery;
import java.util.Optional;

public interface PredictionQueryService {
    Optional<Prediction> handle(GetPredictionByIdQuery query);
}
