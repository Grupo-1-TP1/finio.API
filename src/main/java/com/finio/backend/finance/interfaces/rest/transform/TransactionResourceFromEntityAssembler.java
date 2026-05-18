package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.interfaces.rest.resources.TransactionResource;

public class TransactionResourceFromEntityAssembler {
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getAccount().getAccountId(), // Recupera la FK navegando por el objeto JPA
                entity.getCategory().getCategoryId(), // Recupera la FK navegando por el objeto JPA
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate()
        );
    }
}
