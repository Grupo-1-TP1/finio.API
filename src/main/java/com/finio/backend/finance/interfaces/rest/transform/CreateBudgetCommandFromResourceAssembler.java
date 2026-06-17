package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.commands.CreateBudgetCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateBudgetResource;

public class CreateBudgetCommandFromResourceAssembler {
    public static CreateBudgetCommand toCommandFromResource(CreateBudgetResource resource) {
        return new CreateBudgetCommand(
                resource.userId(),
                resource.categoryId(),
                resource.amount(),
                resource.date()
        );
    }
}
