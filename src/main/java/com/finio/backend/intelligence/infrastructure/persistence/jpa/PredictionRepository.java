package com.finio.backend.intelligence.infrastructure.persistence.jpa;

import com.finio.backend.intelligence.domain.model.aggregates.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    // Retorna la predicción asociada a una transacción específica si existe
    Optional<Prediction> findByTransactionId(Long transactionId);
}
