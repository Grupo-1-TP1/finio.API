package com.finio.backend.iam.domain.model.commands;

public record SignInCommand(String email, String password) {
}
