package com.finio.backend.chatbot.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record UserFinancialSnapshotResource(
        BigDecimal totalBalance,
        BigDecimal totalIncomeThisMonth,
        BigDecimal totalExpenseThisMonth,
        Map<String, BigDecimal> spendingByCategory,
        List<RecentTransactionResource> recentTransactions
) {}
