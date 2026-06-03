package com.finio.backend.chatbot.infrastructure.persistence.jpa.repositories;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    /**
     * Fetches the last 10 messages of a specific user ordered chronologically.
     */
    List<ChatMessage> findTop10BySessionIdOrderByIdAsc(String sessionId);
}