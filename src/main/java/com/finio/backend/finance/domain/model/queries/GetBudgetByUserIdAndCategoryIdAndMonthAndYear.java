package com.finio.backend.finance.domain.model.queries;

public record GetBudgetByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year) {
}
