package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.commands.CreateRecurringTransactionCommand;
import com.finio.backend.finance.domain.model.commands.DeleteRecurringTransactionCommand;
import com.finio.backend.finance.domain.services.RecurringTransactionCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.RecurringTransactionRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.SavingGoalRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RecurringTransactionCommandServiceImpl implements RecurringTransactionCommandService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SavingGoalRepository savingGoalRepository;

    public RecurringTransactionCommandServiceImpl(RecurringTransactionRepository recurringTransactionRepository,
                                                  AccountRepository accountRepository,
                                                  CategoryRepository categoryRepository,
                                                  SavingGoalRepository savingGoalRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.savingGoalRepository = savingGoalRepository;
    }

    @Override
    public Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command) {
        var accountOptional = accountRepository.findById(command.accountId());
        var categoryOptional = categoryRepository.findById(command.categoryId());
        var savingGoalOptional = savingGoalRepository.findById(command.savingGoalId());

        if (accountOptional.isEmpty() || categoryOptional.isEmpty() || savingGoalOptional.isEmpty()) {
            return Optional.empty();
        }

        try {
            RecurringTransaction recurringTransaction = new RecurringTransaction(
                    command, accountOptional.get(), categoryOptional.get(), savingGoalOptional.get());
            return Optional.of(recurringTransactionRepository.save(recurringTransaction));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteRecurringTransactionCommand command) {
        if (!recurringTransactionRepository.existsById(command.recurringTransactionId())) {
            return false;
        }
        try {
            recurringTransactionRepository.deleteById(command.recurringTransactionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
