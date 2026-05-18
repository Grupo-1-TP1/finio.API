package com.finio.backend.iam.domain.services;

import com.finio.backend.iam.domain.model.entities.Role;
import com.finio.backend.iam.domain.model.queries.GetAllRolesQuery;
import com.finio.backend.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);

    Optional<Role> handle(GetRoleByNameQuery query);
}
