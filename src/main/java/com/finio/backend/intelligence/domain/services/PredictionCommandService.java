package com.finio.backend.intelligence.domain.services;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.domain.model.commands.CreatePredictionCommand;
import com.finio.backend.intelligence.domain.model.commands.DeletePredictionCommand;
import java.util.Optional;

public interface PredictionCommandService {
    Optional<Prediction> handle(CreatePredictionCommand command);
    boolean handle(DeletePredictionCommand command);
}
