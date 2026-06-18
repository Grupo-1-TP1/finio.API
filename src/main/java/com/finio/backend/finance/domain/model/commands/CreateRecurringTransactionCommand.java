package com.finio.backend.finance.domain.model.commands;

import com.finio.backend.finance.domain.model.valueobjects.Frequency;
import com.finio.backend.finance.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionCommand(
        Long userId,
        Long accountId,
        Long categoryId,
        TransactionType type,
        BigDecimal amount,
        String description,
        Frequency frequency,
        LocalDate nextExecutionDate
) {}
