package com.finio.backend.chatbot.domain.services.outboundports;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Outbound port to fetch financial summary from the Finance Bounded Context.
 */
public interface FinanceContextFacade {
    BigDecimal getUserTotalBalance(Long userId);
    Map<String, BigDecimal> getUserSpendingByCategory(Long userId);
}