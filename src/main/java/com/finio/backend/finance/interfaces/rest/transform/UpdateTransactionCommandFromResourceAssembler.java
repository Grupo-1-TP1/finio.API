package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.commands.UpdateTransactionCommand;
import com.finio.backend.finance.interfaces.rest.resources.UpdateTransactionResource;

public class UpdateTransactionCommandFromResourceAssembler {
    public static UpdateTransactionCommand toCommandFromResource(Long transactionId, UpdateTransactionResource resource) {
        return new UpdateTransactionCommand(transactionId, resource.categoryId(), resource.amount(), resource.description());
    }
}
