package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionResource(
        Long userId,
        Long accountId,
        Long categoryId,
        Long savingGoalId,
        String type, // "INCOME" o "EXPENSE"
        BigDecimal amount,
        String description,
        String frequency,
        LocalDate nextExecutionDate
) {}
