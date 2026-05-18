package com.finio.backend.iam.domain.services;

import com.finio.backend.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
