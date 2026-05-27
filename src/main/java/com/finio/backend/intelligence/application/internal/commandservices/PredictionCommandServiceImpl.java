package com.finio.backend.intelligence.application.internal.commandservices;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import com.finio.backend.intelligence.domain.model.commands.CreatePredictionCommand;
import com.finio.backend.intelligence.domain.model.commands.DeletePredictionCommand;
import com.finio.backend.intelligence.domain.services.PredictionCommandService;
import com.finio.backend.intelligence.infrastructure.persistence.jpa.PredictionRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PredictionCommandServiceImpl implements PredictionCommandService {

    private final PredictionRepository predictionRepository;

    public PredictionCommandServiceImpl(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    @Override
    public Optional<Prediction> handle(CreatePredictionCommand command) {
        try {
            Prediction prediction = new Prediction(command);
            return Optional.of(predictionRepository.save(prediction));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeletePredictionCommand command) {
        if (!predictionRepository.existsById(command.predictionId())) {
            return false;
        }
        try {
            predictionRepository.deleteById(command.predictionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
