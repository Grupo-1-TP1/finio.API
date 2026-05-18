package com.finio.backend.finance.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetCommand(
        Long userId,
        Long categoryId,
        BigDecimal amount,
        LocalDate startDate,
        LocalDate endDate
) {}
