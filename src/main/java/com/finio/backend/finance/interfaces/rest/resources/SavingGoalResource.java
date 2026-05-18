package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingGoalResource(
        Long id,
        Long userId,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline
) {}