package com.finio.backend.finance.infrastructure.persistence.jpa;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {
    List<SavingGoal> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
