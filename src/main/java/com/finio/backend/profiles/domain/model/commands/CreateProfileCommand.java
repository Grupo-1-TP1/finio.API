package com.finio.backend.profiles.domain.model.commands;

public record CreateProfileCommand(String name, Long user_id) {}