package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.commands.CreateRecurringTransactionCommand;
import com.finio.backend.finance.domain.model.commands.DeleteRecurringTransactionCommand;

import java.util.Optional;

public interface RecurringTransactionCommandService {
    Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command);
    boolean handle(DeleteRecurringTransactionCommand command);
}
