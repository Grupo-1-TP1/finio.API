package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import com.finio.backend.finance.domain.model.commands.CreateBudgetCommand;
import com.finio.backend.finance.domain.model.commands.DeleteBudgetCommand;
import com.finio.backend.finance.domain.services.BudgetCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.BudgetRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class BudgetCommandServiceImpl implements BudgetCommandService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public BudgetCommandServiceImpl(BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Budget> handle(CreateBudgetCommand command) {
        var categoryOptional = categoryRepository.findById(command.categoryId());
        if (categoryOptional.isEmpty()) {
            return Optional.empty();
        }

        try {
            Budget budget = new Budget(command, categoryOptional.get());
            return Optional.of(budgetRepository.save(budget));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteBudgetCommand command) {
        if (!budgetRepository.existsById(command.budgetId())) {
            return false;
        }
        try {
            budgetRepository.deleteById(command.budgetId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
