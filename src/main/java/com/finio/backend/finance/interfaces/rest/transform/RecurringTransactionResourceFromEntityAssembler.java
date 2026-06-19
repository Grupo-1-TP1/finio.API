package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.interfaces.rest.resources.RecurringTransactionResource;

import java.util.Optional;

public class RecurringTransactionResourceFromEntityAssembler {
    public static RecurringTransactionResource toResourceFromEntity(RecurringTransaction entity) {
        Long savingGoalId = Optional.ofNullable(entity.getSavingGoal())
                .map(SavingGoal::getSavingGoalId)
                .orElse(null);
        return new RecurringTransactionResource(
                entity.getRecurringTransactionId(),
                entity.getUserId(),
                entity.getAccount().getAccountId(),
                entity.getCategory().getCategoryId(),
                savingGoalId,
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getFrequency().name(),
                entity.getNextExecutionDate()
        );
    }
}
