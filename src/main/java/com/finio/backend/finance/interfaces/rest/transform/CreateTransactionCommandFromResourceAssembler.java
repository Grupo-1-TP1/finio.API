package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.TransactionType;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateTransactionResource;

public class CreateTransactionCommandFromResourceAssembler {
    public static CreateTransactionCommand toCommandFromResource(CreateTransactionResource resource) {
        return new CreateTransactionCommand(
                resource.userId(),
                resource.accountId(),
                resource.categoryId(),
                TransactionType.valueOf(resource.type().toUpperCase()),
                resource.amount(),
                resource.description(),
                resource.transactionDate()
        );
    }
}