package com.finio.backend.finance.domain.model.commands;

import com.finio.backend.finance.domain.model.aggregates.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionCommand(
        Long userId,
        Long accountId,
        Long categoryId,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate transactionDate
) {}