package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.Budget;
import com.finio.backend.finance.interfaces.rest.resources.BudgetResource;

public class BudgetResourceFromEntityAssembler {
    public static BudgetResource toResourceFromEntity(Budget entity) {
        return new BudgetResource(
                entity.getBudgetId(),
                entity.getUserId(),
                entity.getCategory().getCategoryId(),
                entity.getAmount(),
                entity.getSpent(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
