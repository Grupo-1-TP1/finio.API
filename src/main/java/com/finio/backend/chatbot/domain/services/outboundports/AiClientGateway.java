package com.finio.backend.chatbot.domain.services.outboundports;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.interfaces.rest.resources.UserFinancialSnapshotResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AiClientGateway {
    String generateResponse(
            List<ChatMessage> conversationHistory,
            UserFinancialSnapshotResource financialSnapshot,
            Double savingPercentage
    );
}