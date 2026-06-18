package com.finio.backend.finance.infrastructure.persistence.jpa;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByUserId(Long userId);
    List<RecurringTransaction> findByNextExecutionDateLessThanEqual(LocalDate date);
    void deleteByUserId(Long userId);
}
