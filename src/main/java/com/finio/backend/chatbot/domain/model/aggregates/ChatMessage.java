package com.finio.backend.chatbot.domain.model.aggregates;

import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * ChatMessage Aggregate Root representing a message in the chat history.
 */
@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends AuditableAbstractAggregateRoot<ChatMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "session_id", length = 50)
    private String sessionId;

    @NotBlank
    @Column(length = 20)
    private String role; // "user" o "assistant"

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    public ChatMessage() {}

    public ChatMessage(Long userId, String sessionId, String role, String content) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }
}