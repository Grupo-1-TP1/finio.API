package com.finio.backend.profiles.interfaces.rest.resources;

public record UpdatePrivacyPermissionsResource(Boolean allow_ml_analysis,
                                               Boolean allow_push_notifications,
                                               Boolean use_biometrics) {
}
