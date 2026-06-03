package com.finio.backend.chatbot.application.internal.commandservices;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.domain.model.commands.SendMessageCommand;
import com.finio.backend.chatbot.domain.services.outboundports.AiClientGateway;
import com.finio.backend.chatbot.domain.services.outboundports.FinanceContextFacade;
import com.finio.backend.chatbot.infrastructure.persistence.jpa.repositories.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatCommandServiceImpl {

    private final ChatMessageRepository chatMessageRepository;
    private final AiClientGateway aiClientGateway;
    private final FinanceContextFacade financeContextFacade; // Inyectamos la fachada de finanzas

    public ChatCommandServiceImpl(ChatMessageRepository chatMessageRepository, AiClientGateway aiClientGateway, FinanceContextFacade financeContextFacade) {
        this.chatMessageRepository = chatMessageRepository;
        this.aiClientGateway = aiClientGateway;
        this.financeContextFacade = financeContextFacade;
    }

    @Transactional
    public ChatMessage handle(SendMessageCommand command) {
        // 1. Guardar el mensaje del usuario
        ChatMessage userMessage = new ChatMessage(command.userId(), command.sessionId(), "user", command.messageContent());
        chatMessageRepository.save(userMessage);

        // 🔥 SOLUCIÓN 1: Forzamos a Hibernate a escribir IMMEDIATAMENTE en la BD
        // Esto asegura que el mensaje actual y los anteriores estén perfectamente asentados en las tablas.
        chatMessageRepository.flush();

        // 2. Recuperar el historial contextual (últimos 10 mensajes)
        List<ChatMessage> history = chatMessageRepository.findTop10BySessionIdOrderByIdAsc(command.sessionId());

        // 3. Traer la información financiera real
        var balance = financeContextFacade.getUserTotalBalance(command.userId());
        var spending = financeContextFacade.getUserSpendingByCategory(command.userId());

        // 4. Consultar a OpenAI pasando el historial verificado
        String aiResponseContent = aiClientGateway.generateResponse(history, balance, spending);

        // 5. Guardar respuesta del bot
        ChatMessage aiMessage = new ChatMessage(command.userId(), command.sessionId(), "assistant", aiResponseContent);
        chatMessageRepository.save(aiMessage);

        return aiMessage;
    }
}