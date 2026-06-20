package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionResource(
        Long userId,
        Long accountId,
        Long categoryId,
        Long savingGoalId,
        String type,
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        Double confidence,
        Long predictedCategoryId
) {}