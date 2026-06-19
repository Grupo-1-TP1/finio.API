package com.finio.backend.finance.infrastructure.persistence.jpa;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findBySavingGoal_SavingGoalId(Long savingGoalSavingGoalId);
    void deleteByUserId(Long userId);
}
