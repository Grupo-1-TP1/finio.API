package com.finio.backend.finance.domain.model.events;

import java.math.BigDecimal;

public record BudgetLimitReachedEvent(
        Long userId,
        String categoryName,
        BigDecimal amount,
        BigDecimal spent,
        String alertType // "WARNING_80" o "EXCEEDED_100"
) {}