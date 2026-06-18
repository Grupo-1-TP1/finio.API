package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.commands.UpdateProfileCommand;
import com.finio.backend.profiles.interfaces.rest.resources.UpdateProfileResource;

public class UpdateProfileCommandFromResourceAssembler {
    public static UpdateProfileCommand toCommandFromResource(Long userId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(userId, resource.name(), resource.saving_percentage(), resource.use_ml_analysis(), resource.allow_push_notifications(), resource.use_biometrics());
    }
}
