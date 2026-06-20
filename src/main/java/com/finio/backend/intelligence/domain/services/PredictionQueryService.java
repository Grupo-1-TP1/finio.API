package com.finio.backend.intelligence.domain.services;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.domain.model.queries.GetAllPredictionsQuery;
import com.finio.backend.intelligence.domain.model.queries.GetPredictionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PredictionQueryService {
    Optional<Prediction> handle(GetPredictionByIdQuery query);
    List<Prediction> handle(GetAllPredictionsQuery query);
}
