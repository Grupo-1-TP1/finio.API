package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.commands.CreateAccountCommand;
import com.finio.backend.finance.interfaces.rest.resources.CreateAccountResource;

public class CreateAccountCommandFromResourceAssembler {
    public static CreateAccountCommand toCommandFromResource(CreateAccountResource resource) {
        return new CreateAccountCommand(
                resource.userId(),
                resource.name(),
                resource.balance()
        );
    }
}