package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingGoalResource(
        Long userId,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline
) {}
