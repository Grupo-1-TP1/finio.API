package com.finio.backend.iam.domain.model.commands;

public record ResetPasswordCommand(String email, String newPassword) {
}
