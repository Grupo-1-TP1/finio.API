package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import com.finio.backend.finance.domain.model.queries.GetBudgetByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetBudgetByUserIdAndCategoryIdAndMonthAndYear;
import com.finio.backend.finance.domain.model.queries.GetBudgetsByUserIdAndMonthAndYear;
import com.finio.backend.finance.domain.model.queries.GetBudgetsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface BudgetQueryService {
    Optional<Budget> handle(GetBudgetByIdQuery query);
    List<Budget> handle(GetBudgetsByUserIdQuery query);
    Optional<Budget> handle(GetBudgetByUserIdAndCategoryIdAndMonthAndYear query);
    List<Budget> handle(GetBudgetsByUserIdAndMonthAndYear query);
}
