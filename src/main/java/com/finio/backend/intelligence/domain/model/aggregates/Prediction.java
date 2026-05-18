package com.finio.backend.intelligence.domain.model.aggregates;

import com.finio.backend.intelligence.domain.model.commands.CreatePredictionCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prediction extends AuditableAbstractAggregateRoot<Prediction> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Long predictionId;

    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    public Prediction(CreatePredictionCommand command) {
        this.confidenceScore = command.confidenceScore();
        this.categoryId = command.categoryId();
        this.transactionId = command.transactionId();
    }
}