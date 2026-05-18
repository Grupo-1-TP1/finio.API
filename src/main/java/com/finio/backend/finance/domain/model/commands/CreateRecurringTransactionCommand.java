package com.finio.backend.finance.domain.model.commands;

import com.finio.backend.finance.domain.model.aggregates.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionCommand(
        Long userId,
        Long accountId,
        Long categoryId,
        TransactionType type,
        BigDecimal amount,
        String description,
        String frequency, // Ej: "MONTHLY", "WEEKLY"
        LocalDate nextExecutionDate
) {}
