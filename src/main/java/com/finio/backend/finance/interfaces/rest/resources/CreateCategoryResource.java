package com.finio.backend.finance.interfaces.rest.resources;

public record CreateCategoryResource(
        String name,
        String description
) {}
