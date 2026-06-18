package com.finio.backend.iam.interfaces.rest.transform;

import com.finio.backend.iam.domain.model.commands.ResetPasswordCommand;
import com.finio.backend.iam.interfaces.rest.resources.ResetPasswordResource;

public class ResetPasswordCommandFromResourceAssembler {
    public static ResetPasswordCommand toCommandFromResource(Long userId, ResetPasswordResource resource) {
        return new ResetPasswordCommand(userId, resource.newPassword());
    }
}
