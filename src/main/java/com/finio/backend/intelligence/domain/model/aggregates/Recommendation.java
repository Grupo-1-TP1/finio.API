package com.finio.backend.intelligence.domain.model.aggregates;

import com.finio.backend.intelligence.domain.model.commands.CreateRecommendationCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation extends AuditableAbstractAggregateRoot<Recommendation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "projected_savings", nullable = false)
    private Double projectedSavings;

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommendationDetail> details = new ArrayList<>();

    public Recommendation(CreateRecommendationCommand command) {
        this.userId = command.userId();
        this.projectedSavings = command.projectedSavings();
    }

    public void addDetail(RecommendationDetail detail) {
        this.details.add(detail);
        detail.setRecommendation(this);
    }
}