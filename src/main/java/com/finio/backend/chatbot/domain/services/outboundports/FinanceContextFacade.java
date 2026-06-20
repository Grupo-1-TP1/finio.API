package com.finio.backend.chatbot.domain.services.outboundports;

import com.finio.backend.chatbot.interfaces.rest.resources.RecentTransactionResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Outbound port to fetch financial summary from the Finance Bounded Context.
 */
public interface FinanceContextFacade {
    BigDecimal getUserTotalBalance(Long userId);
    BigDecimal getUserTotalIncomeThisMonth(Long userId);
    BigDecimal getUserTotalExpenseThisMonth(Long userId);
    List<RecentTransactionResource> getRecentTransactions(Long userId, int amount);
    Double getUserSavingPercentage(Long userId);
    Map<String, BigDecimal> getUserSpendingByCategory(Long userId);
}