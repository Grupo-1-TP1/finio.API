package com.finio.backend.chatbot.interfaces.rest.resources;

import java.math.BigDecimal;

public record RecentTransactionResource(
        String date,
        String description,
        String type, // "INGRESO" o "GASTO"
        BigDecimal amount,
        String category
) {
}
