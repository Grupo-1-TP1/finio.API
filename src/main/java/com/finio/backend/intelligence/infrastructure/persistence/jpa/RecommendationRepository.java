package com.finio.backend.intelligence.infrastructure.persistence.jpa;

import com.finio.backend.intelligence.domain.model.aggregates.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserId(Long userId);
}
