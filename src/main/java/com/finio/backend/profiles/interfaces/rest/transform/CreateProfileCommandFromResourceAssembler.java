package com.finio.backend.profiles.interfaces.rest.transform;

import com.finio.backend.profiles.domain.model.commands.CreateProfileCommand;
import com.finio.backend.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.name(),
                resource.user_id()
        );
    }
}
