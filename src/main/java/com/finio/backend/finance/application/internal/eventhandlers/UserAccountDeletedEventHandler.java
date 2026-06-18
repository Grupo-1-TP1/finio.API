package com.finio.backend.finance.application.internal.eventhandlers;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.iam.domain.model.events.UserAccountDeletedEvent;
import com.finio.backend.finance.infrastructure.persistence.jpa.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserAccountDeletedEventHandler {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public UserAccountDeletedEventHandler(TransactionRepository transactionRepository,
                                          BudgetRepository budgetRepository,
                                          AccountRepository accountRepository,
                                          SavingGoalRepository savingGoalRepository,
                                          RecurringTransactionRepository recurringTransactionRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @EventListener
    @Transactional
    public void on(UserAccountDeletedEvent event) {
        Long userId = event.userId();

        transactionRepository.deleteByUserId(userId);
        budgetRepository.deleteByUserId(userId);
        recurringTransactionRepository.deleteByUserId(userId);
        savingGoalRepository.deleteByUserId(userId);
        accountRepository.deleteByUserId(userId);

        System.out.println("🗑️ [Finance] Datos transaccionales limpios en Azure para el userId: " + userId);
    }
}