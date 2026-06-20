package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.commands.UpdateSavingPercentageCommand;
import com.finio.backend.profiles.interfaces.rest.resources.UpdateSavingPercentageResource;

public class UpdateSavingPercentageCommandFromResourceAssembler {
    public static UpdateSavingPercentageCommand toCommandFromResource(Long userId, UpdateSavingPercentageResource resource) {
        return new UpdateSavingPercentageCommand(userId, resource.percentage());
    }
}
