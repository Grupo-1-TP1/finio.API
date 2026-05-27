package com.finio.backend.iam.interfaces.rest.transform;

import com.finio.backend.iam.domain.model.aggregates.User;
import com.finio.backend.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token, String role) {
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), token, role);
    }
}
