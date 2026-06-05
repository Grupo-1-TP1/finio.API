package com.finio.backend.finance.domain.model.events;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionCreatedEvent(
        Long userId,
        Long categoryId,
        String type, // "EXPENSE" o "INCOME"
        BigDecimal amount,
        String description,
        LocalDate transactionDate
) {}