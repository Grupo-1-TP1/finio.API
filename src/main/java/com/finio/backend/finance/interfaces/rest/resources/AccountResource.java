package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;

public record AccountResource(
        Long id,
        Long userId,
        String name,
        BigDecimal balance,
        BigDecimal availableBalance,
        BigDecimal savingsFund,
        BigDecimal savingPercentage
) {}
