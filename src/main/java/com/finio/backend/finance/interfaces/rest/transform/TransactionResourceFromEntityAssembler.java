package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.interfaces.rest.resources.TransactionResource;

public class TransactionResourceFromEntityAssembler {
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getAccount().getAccountId(),
                entity.getCategory().getCategoryId(),
                entity.getSavingGoal().getSavingGoalId(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate()
        );
    }
}
