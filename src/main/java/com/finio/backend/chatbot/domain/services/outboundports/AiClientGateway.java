package com.finio.backend.chatbot.domain.services.outboundports;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import java.util.List;

/**
 * Outbound port for communicating with the AI service.
 */
public interface AiClientGateway {
    /**
     * Generates a response from the AI model based on the conversation history.
     * @param conversationHistory List of past chat messages for context
     * @return The text response from the AI
     */
    String generateResponse(List<ChatMessage> conversationHistory);
}