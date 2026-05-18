package com.finio.backend.finance.infrastructure.persistence.jpa;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
}
