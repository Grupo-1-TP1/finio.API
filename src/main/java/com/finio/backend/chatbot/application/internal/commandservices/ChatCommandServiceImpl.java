package com.finio.backend.chatbot.application.internal.commandservices;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.domain.model.commands.SendMessageCommand;
import com.finio.backend.chatbot.domain.services.outboundports.AiClientGateway;
import com.finio.backend.chatbot.infrastructure.persistence.jpa.repositories.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatCommandServiceImpl {

    private final ChatMessageRepository chatMessageRepository;
    private final AiClientGateway aiClientGateway;

    public ChatCommandServiceImpl(ChatMessageRepository chatMessageRepository, AiClientGateway aiClientGateway) {
        this.chatMessageRepository = chatMessageRepository;
        this.aiClientGateway = aiClientGateway;
    }

    @Transactional
    public ChatMessage handle(SendMessageCommand command) {
        // 1. Guardar el mensaje del usuario en la BD
        ChatMessage userMessage = new ChatMessage(command.userId(), "user", command.messageContent());
        chatMessageRepository.save(userMessage);

        // 2. Recuperar el historial contextual (últimos 10 mensajes)
        List<ChatMessage> history = chatMessageRepository.findTop10ByUserIdOrderByIdAsc(command.userId());

        // 3. Consultar a OpenAI pasando el historial estructurado
        String aiResponseContent = aiClientGateway.generateResponse(history);

        // 4. Guardar la respuesta generada por la IA
        ChatMessage aiMessage = new ChatMessage(command.userId(), "assistant", aiResponseContent);
        chatMessageRepository.save(aiMessage);

        return aiMessage;
    }
}