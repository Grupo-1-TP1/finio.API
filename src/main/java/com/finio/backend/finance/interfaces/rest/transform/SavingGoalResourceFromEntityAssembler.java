package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.interfaces.rest.resources.SavingGoalResource;

public class SavingGoalResourceFromEntityAssembler {
    public static SavingGoalResource toResourceFromEntity(SavingGoal entity) {
        return new SavingGoalResource(
                entity.getSavingGoalId(),
                entity.getUserId(),
                entity.getName(),
                entity.getTargetAmount(),
                entity.getCurrentAmount(),
                entity.getDeadline()
        );
    }
}
