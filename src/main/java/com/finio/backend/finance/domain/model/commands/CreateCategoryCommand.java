package com.finio.backend.finance.domain.model.commands;

public record CreateCategoryCommand(
        String name,
        String description
) {}
