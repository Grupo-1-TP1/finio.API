package com.finio.backend.iam.interfaces.rest.transform;

import com.finio.backend.iam.domain.model.commands.ResetPasswordCommand;
import com.finio.backend.iam.interfaces.rest.resources.ResetPasswordResource;

public class ResetPasswordCommandFromResourceAssembler {
    public static ResetPasswordCommand toCommandFromResource(String email, ResetPasswordResource resource) {
        return new ResetPasswordCommand(email, resource.newPassword());
    }
}
