package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.commands.UpdateProfileCommand;
import com.finio.backend.profiles.domain.model.commands.UpdateProfileNameCommand;
import com.finio.backend.profiles.interfaces.rest.resources.UpdateProfileNameResource;

public class UpdateProfileNameCommandFromResourceAssembler {
    public static UpdateProfileNameCommand toCommandFromResource(Long userId, UpdateProfileNameResource resource) {
        return new UpdateProfileNameCommand(userId, resource.name());
    }
}
