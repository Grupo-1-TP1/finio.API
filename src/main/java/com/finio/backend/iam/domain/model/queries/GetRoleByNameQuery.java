package com.finio.backend.iam.domain.model.queries;

import com.finio.backend.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}
