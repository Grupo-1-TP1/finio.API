package com.finio.backend.intelligence.application.internal.queryservices;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.domain.model.queries.GetPredictionByIdQuery;
import com.finio.backend.intelligence.domain.services.PredictionQueryService;
import com.finio.backend.intelligence.infrastructure.persistence.jpa.PredictionRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PredictionQueryServiceImpl implements PredictionQueryService {

    private final PredictionRepository predictionRepository;

    public PredictionQueryServiceImpl(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @Override
    public Optional<Prediction> handle(GetPredictionByIdQuery query) {
        return predictionRepository.findById(query.predictionId());
    }
}
