package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.interfaces.rest.resources.RecurringTransactionResource;

public class RecurringTransactionResourceFromEntityAssembler {
    public static RecurringTransactionResource toResourceFromEntity(RecurringTransaction entity) {
        return new RecurringTransactionResource(
                entity.getRecurringTransactionId(),
                entity.getUserId(),
                entity.getAccount().getAccountId(),
                entity.getCategory().getCategoryId(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getFrequency(),
                entity.getNextExecutionDate()
        );
    }
}
