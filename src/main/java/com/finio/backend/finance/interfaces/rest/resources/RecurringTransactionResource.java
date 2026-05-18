package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionResource(
        Long id,
        Long userId,
        Long accountId,
        Long categoryId,
        String type,
        BigDecimal amount,
        String description,
        String frequency,
        LocalDate nextExecutionDate
) {}
