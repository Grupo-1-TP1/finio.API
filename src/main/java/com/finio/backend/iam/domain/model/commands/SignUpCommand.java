package com.finio.backend.iam.domain.model.commands;

import com.finio.backend.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String email, String password, List<Role> roles) {
}
