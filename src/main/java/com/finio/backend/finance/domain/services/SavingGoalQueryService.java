package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.queries.GetSavingGoalByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetSavingGoalsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface SavingGoalQueryService {
    Optional<SavingGoal> handle(GetSavingGoalByIdQuery query);
    List<SavingGoal> handle(GetSavingGoalsByUserIdQuery query);
}