package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;

public record CreateAccountResource(
        Long userId,
        String name,
        BigDecimal balance
) {}