package com.finio.backend.finance.domain.model.queries;

public record GetTransactionsByUserIdAndMonthAndYear(Long userId, Integer month, Integer year) {
}
