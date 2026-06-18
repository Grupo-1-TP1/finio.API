package com.finio.backend.finance.interfaces.rest.resources;

import java.math.BigDecimal;

public record UpdateTransactionResource(Long categoryId, BigDecimal amount, String description) {
}
