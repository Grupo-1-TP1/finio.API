package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.commands.CreateSavingGoalCommand;
import com.finio.backend.finance.domain.model.commands.DeleteSavingGoalCommand;

import java.util.Optional;

public interface SavingGoalCommandService {
    Optional<SavingGoal> handle(CreateSavingGoalCommand command);
    boolean handle(DeleteSavingGoalCommand command);
}
