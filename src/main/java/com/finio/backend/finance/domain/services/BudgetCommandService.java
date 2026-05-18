package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import com.finio.backend.finance.domain.model.commands.CreateBudgetCommand;
import com.finio.backend.finance.domain.model.commands.DeleteBudgetCommand;

import java.util.Optional;

public interface BudgetCommandService {
    Optional<Budget> handle(CreateBudgetCommand command);
    boolean handle(DeleteBudgetCommand command);
}