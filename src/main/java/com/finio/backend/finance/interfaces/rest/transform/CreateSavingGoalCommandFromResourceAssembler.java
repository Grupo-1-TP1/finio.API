package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.commands.CreateSavingGoalCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateSavingGoalResource;

public class CreateSavingGoalCommandFromResourceAssembler {
    public static CreateSavingGoalCommand toCommandFromResource(CreateSavingGoalResource resource) {
        return new CreateSavingGoalCommand(
                resource.userId(),
                resource.name(),
                resource.targetAmount(),
                resource.currentAmount(),
                resource.deadline()
        );
    }
}
