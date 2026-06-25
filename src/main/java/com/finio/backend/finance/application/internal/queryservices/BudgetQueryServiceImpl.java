package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import com.finio.backend.finance.domain.model.queries.GetBudgetByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetBudgetByUserIdAndCategoryIdAndMonthAndYear;
import com.finio.backend.finance.domain.model.queries.GetBudgetsByUserIdAndMonthAndYear;
import com.finio.backend.finance.domain.model.queries.GetBudgetsByUserIdQuery;
import com.finio.backend.finance.domain.services.BudgetQueryService;
import com.finio.backend.finance.infrastructure.persistence.jpa.BudgetRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetQueryServiceImpl implements BudgetQueryService {

    private final BudgetRepository budgetRepository;

    public BudgetQueryServiceImpl(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Optional<Budget> handle(GetBudgetByIdQuery query) {
        return budgetRepository.findById(query.budgetId());
    }

    @Override
    public List<Budget> handle(GetBudgetsByUserIdQuery query) {
        return budgetRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Budget> handle(GetBudgetByUserIdAndCategoryIdAndMonthAndYear query) { return budgetRepository.findByUserIdAndCategory_CategoryIdAndMonthAndYear(query.userId(), query.categoryId(), query.month(), query.year()); }

    @Override
    public List<Budget> handle(GetBudgetsByUserIdAndMonthAndYear query) {
        return budgetRepository.findByUserIdAndMonthAndYear(query.userId(), query.month(), query.year());
    }
}
