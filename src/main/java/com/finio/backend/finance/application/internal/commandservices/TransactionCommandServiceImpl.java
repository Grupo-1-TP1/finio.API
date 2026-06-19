package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.commands.UpdateTransactionCommand;
import com.finio.backend.finance.domain.model.valueobjects.TransactionType;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.domain.model.commands.DeleteTransactionCommand;
import com.finio.backend.finance.domain.model.events.TransactionCreatedEvent;
import com.finio.backend.finance.domain.services.TransactionCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.SavingGoalRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.TransactionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository,
                                         AccountRepository accountRepository,
                                         CategoryRepository categoryRepository,
                                         ApplicationEventPublisher eventPublisher,
                                         SavingGoalRepository savingGoalRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(CreateTransactionCommand command) {

        Optional<Account> accountOptional = accountRepository.findById(command.accountId());
        Optional<Category> categoryOptional = categoryRepository.findById(command.categoryId());

        if (accountOptional.isEmpty() || categoryOptional.isEmpty()) {
            return Optional.empty();
        }

        Account account = accountOptional.get();
        Category category = categoryOptional.get();
        SavingGoal savingGoal = null;

        if (command.savingGoalId() != null) {
            Optional<SavingGoal> savingGoalOptional = savingGoalRepository.findById(command.savingGoalId());
            if (savingGoalOptional.isPresent()) {
                savingGoal = savingGoalOptional.get();
            }
        }

        if (command.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(command.amount()));
            if (savingGoal != null) {
                savingGoal.setCurrentAmount(savingGoal.getCurrentAmount().add(command.amount()));
                savingGoalRepository.save(savingGoal);
            }
        } else if (command.type() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(command.amount()));
        }
        accountRepository.save(account);

        Transaction transaction = new Transaction(command, account, category, savingGoal);
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);

            if (savedTransaction.getType() == TransactionType.EXPENSE) {
                var event = new TransactionCreatedEvent(
                        account.getUserId(),
                        category.getCategoryId(),
                        savedTransaction.getType().name(),
                        savedTransaction.getAmount(),
                        savedTransaction.getDescription(),
                        savedTransaction.getTransactionDate()
                );
                eventPublisher.publishEvent(event);
            }

            return Optional.of(savedTransaction);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(UpdateTransactionCommand command) {
        Transaction transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setCategory(categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found")));

        transaction.setAmount(command.amount());
        transaction.setDescription(command.description());
        return Optional.of(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public boolean handle(DeleteTransactionCommand command) {
        var transactionOptional = transactionRepository.findById(command.transactionId());
        if (transactionOptional.isEmpty()) {
            return false;
        }

        try {
            // Regla de negocio: Al eliminar una transacción, revertimos el saldo de la cuenta antes de borrarla
            var transaction = transactionOptional.get();
            var account = transaction.getAccount();
            if (transaction.getType() == TransactionType.EXPENSE) {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            } else {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
            accountRepository.save(account);

            transactionRepository.deleteById(command.transactionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}