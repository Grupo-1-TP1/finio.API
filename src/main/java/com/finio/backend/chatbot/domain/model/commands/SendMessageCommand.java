package com.finio.backend.chatbot.domain.model.commands;

/**
 * Command to handle a new message sent by the user to the chatbot.
 */
public record SendMessageCommand(Long userId, String messageContent) {
    public SendMessageCommand {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (messageContent == null || messageContent.isBlank()) throw new IllegalArgumentException("Message content cannot be empty");
    }
}