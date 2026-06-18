package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetResource(
        Long userId,
        Long categoryId,
        BigDecimal amount,
        LocalDate date
) {}
