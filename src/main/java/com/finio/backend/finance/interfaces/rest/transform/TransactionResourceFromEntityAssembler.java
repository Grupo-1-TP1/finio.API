package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.interfaces.rest.resources.TransactionResource;

import java.util.Optional;

public class TransactionResourceFromEntityAssembler {
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        Long savingGoalId = Optional.ofNullable(entity.getSavingGoal())
                .map(SavingGoal::getSavingGoalId)
                .orElse(null);
        return new TransactionResource(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getAccount().getAccountId(),
                entity.getCategory().getCategoryId(),
                savingGoalId,
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate()
        );
    }
}
