package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.commands.UpdatePrivacyPermissionsCommand;
import com.finio.backend.profiles.interfaces.rest.resources.UpdatePrivacyPermissionsResource;

public class UpdatePrivacyPermissionsCommandFromResourceAssembler {
    public static UpdatePrivacyPermissionsCommand toCommandFromResource(Long userId, UpdatePrivacyPermissionsResource resource) {
        return new UpdatePrivacyPermissionsCommand(userId, resource.allow_ml_analysis(), resource.allow_push_notifications(), resource.use_biometrics());
    }
}
