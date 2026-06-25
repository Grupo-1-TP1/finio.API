package com.finio.backend.finance.domain.model.queries;

public record GetBudgetsByUserIdAndMonthAndYear(Long userId, Integer month, Integer year) {
}
