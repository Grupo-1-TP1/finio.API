package com.finio.backend.finance.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSavingGoalCommand(
        Long userId,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate deadline
) {}
