package com.finio.backend.chatbot.application.internal.commandservices;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.domain.model.commands.SendMessageCommand;
import com.finio.backend.chatbot.domain.services.outboundports.AiClientGateway;
import com.finio.backend.chatbot.domain.services.outboundports.FinanceContextFacade;
import com.finio.backend.chatbot.infrastructure.persistence.jpa.repositories.ChatMessageRepository;
import com.finio.backend.chatbot.interfaces.rest.resources.RecentTransactionResource;
import com.finio.backend.chatbot.interfaces.rest.resources.UserFinancialSnapshotResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChatCommandServiceImpl {

    private final ChatMessageRepository chatMessageRepository;
    private final AiClientGateway aiClientGateway;
    private final FinanceContextFacade financeContextFacade;

    public ChatCommandServiceImpl(ChatMessageRepository chatMessageRepository, AiClientGateway aiClientGateway, FinanceContextFacade financeContextFacade) {
        this.chatMessageRepository = chatMessageRepository;
        this.aiClientGateway = aiClientGateway;
        this.financeContextFacade = financeContextFacade;
    }

    @Transactional
    public ChatMessage handle(SendMessageCommand command) {
        ChatMessage userMessage = new ChatMessage(command.userId(), command.sessionId(), "user", command.messageContent());
        chatMessageRepository.save(userMessage);

        chatMessageRepository.flush();

        List<ChatMessage> history = chatMessageRepository.findTop10BySessionIdOrderByIdAsc(command.sessionId());

        var totalBalance = financeContextFacade.getUserTotalBalance(command.userId());
        var totalIncome = financeContextFacade.getUserTotalIncomeThisMonth(command.userId());   // 🔥 Agregado real
        var totalExpense = financeContextFacade.getUserTotalExpenseThisMonth(command.userId()); // 🔥 Agregado real
        var spendingCategory = financeContextFacade.getUserSpendingByCategory(command.userId());

        List<RecentTransactionResource> recentTransactions = financeContextFacade.getRecentTransactions(command.userId(), 10); // 🔥 Agregado real

        Double savingPercentage = financeContextFacade.getUserSavingPercentage(command.userId()); // 🔥 Agregado real

        UserFinancialSnapshotResource financialSnapshot = new UserFinancialSnapshotResource(
                totalBalance,
                totalIncome,
                totalExpense,
                spendingCategory,
                recentTransactions
        );

        String aiResponseContent = aiClientGateway.generateResponse(history, financialSnapshot, savingPercentage);

        ChatMessage aiMessage = new ChatMessage(command.userId(), command.sessionId(), "assistant", aiResponseContent);
        chatMessageRepository.save(aiMessage);

        return aiMessage;
    }
}