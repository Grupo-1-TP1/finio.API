package com.finio.backend.finance.interfaces.rest.resources;

public record CategoryResource(
        Long id,
        String name,
        String description
) {}
