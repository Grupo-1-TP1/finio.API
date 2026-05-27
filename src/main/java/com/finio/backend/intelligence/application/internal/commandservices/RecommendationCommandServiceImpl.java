package com.finio.backend.intelligence.application.internal.commandservices;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import com.finio.backend.intelligence.domain.model.aggregates.RecommendationDetail;
import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationCommand;
import com.finio.backend.intelligence.domain.model.commands.DeleteRecommendationCommand;
import com.finio.backend.intelligence.domain.services.RecommendationCommandService;
import com.finio.backend.intelligence.infrastructure.persistence.jpa.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class RecommendationCommandServiceImpl implements RecommendationCommandService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationCommandServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    @Transactional
    public Optional<Recommendation> handle(CreateRecommendationCommand command) {
        try {
            // 1. Instanciamos el objeto maestro (Recommendation)
            Recommendation recommendation = new Recommendation(command);

            // 2. Mapeamos y agregamos los detalles (RecommendationDetail) vinculándolos al padre
            if (command.details() != null) {
                for (var detailCommand : command.details()) {
                    RecommendationDetail detail = new RecommendationDetail(
                            detailCommand.categoryId(),
                            detailCommand.currentLimit(),
                            detailCommand.suggestedLimit()
                    );
                    recommendation.addDetail(detail);
                }
            }

            // 3. Al guardar el maestro, JPA persistirá los hijos en cascada automáticamente
            return Optional.of(recommendationRepository.save(recommendation));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteRecommendationCommand command) {
        if (!recommendationRepository.existsById(command.recommendationId())) {
            return false;
        }
        try {
            recommendationRepository.deleteById(command.recommendationId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
