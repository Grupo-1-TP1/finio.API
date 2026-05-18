package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.TransactionType;
import com.finio.backend.finance.domain.model.commands.CreateRecurringTransactionCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateRecurringTransactionResource;

public class CreateRecurringTransactionCommandFromResourceAssembler {
    public static CreateRecurringTransactionCommand toCommandFromResource(CreateRecurringTransactionResource resource) {
        return new CreateRecurringTransactionCommand(
                resource.userId(),
                resource.accountId(),
                resource.categoryId(),
                TransactionType.valueOf(resource.type().toUpperCase()),
                resource.amount(),
                resource.description(),
                resource.frequency(),
                resource.nextExecutionDate()
        );
    }
}
