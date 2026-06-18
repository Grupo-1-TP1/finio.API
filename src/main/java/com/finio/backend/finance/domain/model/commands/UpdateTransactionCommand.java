package com.finio.backend.finance.domain.model.commands;

import java.math.BigDecimal;

public record UpdateTransactionCommand(Long transactionId, Long categoryId, BigDecimal amount, String description) {
}
