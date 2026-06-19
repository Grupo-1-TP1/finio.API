package com.finio.backend.finance.application.internal.scheduling;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.application.internal.commandservices.TransactionCommandServiceImpl; // Ajusta el import real
import com.finio.backend.finance.infrastructure.persistence.jpa.RecurringTransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecurringTransactionScheduler {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionCommandServiceImpl transactionCommandService;

    public RecurringTransactionScheduler(RecurringTransactionRepository recurringTransactionRepository,
                                         TransactionCommandServiceImpl transactionCommandService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionCommandService = transactionCommandService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processRecurringTransactions() {
        LocalDate today = LocalDate.now();

        List<RecurringTransaction> pendingTransactions =
                recurringTransactionRepository.findByNextExecutionDateLessThanEqual(today);

        for (RecurringTransaction recurring : pendingTransactions) {
            try {
                CreateTransactionCommand transactionCommand = new CreateTransactionCommand(
                        recurring.getUserId(),
                        recurring.getAccount().getAccountId(),
                        recurring.getCategory().getCategoryId(),
                        recurring.getSavingGoal().getSavingGoalId(),
                        recurring.getType(), // "INCOME" o "EXPENSE"
                        recurring.getAmount(),
                        "[Recurrente] " + (recurring.getDescription() != null ? recurring.getDescription() : ""),
                        today
                );

                transactionCommandService.handle(transactionCommand);

                recurring.updateNextExecutionDate();
                recurringTransactionRepository.save(recurring);

            } catch (Exception e) {
                System.err.println("Error processing recurring transaction ID " + recurring.getRecurringTransactionId() + ": " + e.getMessage());
            }
        }
    }
}