package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResource(
        Long id,
        Long userId,
        Long categoryId,
        BigDecimal amount,
        BigDecimal spent,
        LocalDate date,
        Integer month,
        Integer year
) {}
