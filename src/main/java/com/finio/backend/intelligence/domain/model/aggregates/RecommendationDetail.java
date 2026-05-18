package com.finio.backend.intelligence.domain.model.aggregates;

import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationDetailCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendations_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDetail extends AuditableAbstractAggregateRoot<RecommendationDetail> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private Recommendation recommendation;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "current_limit", nullable = false)
    private Double currentLimit;

    @Column(name = "suggested_limit", nullable = false)
    private Double suggestedLimit;

    public RecommendationDetail(Long categoryId, Double currentLimit, Double suggestedLimit) {
        this.categoryId = categoryId;
        this.currentLimit = currentLimit;
        this.suggestedLimit = suggestedLimit;
    }

    public RecommendationDetail(CreateRecommendationDetailCommand command) {
        this.categoryId = command.categoryId();
        this.currentLimit = command.currentLimit();
        this.suggestedLimit = command.suggestedLimit();
    }
}
