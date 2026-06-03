package com.finio.backend.chatbot.domain.model.commands;

/**
 * Command to handle a new message sent by the user to the chatbot.
 */
public record SendMessageCommand(Long userId, String sessionId, String messageContent) {
    public SendMessageCommand {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("Session ID cannot be empty");
        if (messageContent == null || messageContent.isBlank()) throw new IllegalArgumentException("Message content cannot be empty");
    }
}