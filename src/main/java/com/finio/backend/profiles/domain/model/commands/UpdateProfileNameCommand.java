package com.finio.backend.profiles.domain.model.commands;

public record UpdateProfileNameCommand(Long userId, String name) {
}
