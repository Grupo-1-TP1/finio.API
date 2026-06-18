package com.finio.backend.iam.domain.model.commands;

public record ResetPasswordCommand(Long userId, String newPassword) {
}
