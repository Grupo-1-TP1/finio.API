package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.domain.model.commands.DeleteTransactionCommand;
import com.finio.backend.finance.domain.model.commands.UpdateTransactionCommand;

import java.util.Optional;

public interface TransactionCommandService {
    Optional<Transaction> handle(CreateTransactionCommand command);
    boolean handle(DeleteTransactionCommand command);
    Optional<Transaction> handle(UpdateTransactionCommand command);
}
