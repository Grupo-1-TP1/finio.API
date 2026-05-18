package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.commands.CreateCategoryCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateCategoryResource;

public class CreateCategoryCommandFromResourceAssembler {
    public static CreateCategoryCommand toCommandFromResource(CreateCategoryResource resource) {
        return new CreateCategoryCommand(
                resource.name(),
                resource.description()
        );
    }
}
