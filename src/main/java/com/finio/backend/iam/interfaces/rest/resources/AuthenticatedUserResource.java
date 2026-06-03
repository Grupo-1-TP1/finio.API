package com.finio.backend.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(Long id, String email, String token, String role) {
}
