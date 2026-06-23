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
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository,
                                         AccountRepository accountRepository,
                                         CategoryRepository categoryRepository,
                                         ApplicationEventPublisher eventPublisher,
                                         SavingGoalRepository savingGoalRepository,
                                         ProfileRepository profileRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.profileRepository = profileRepository;
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

        BigDecimal savingPercentage = profileRepository.findByUserId(account.getUserId())
                .map(Profile::getSaving_percentage)
                .orElse(BigDecimal.ZERO);

        if (command.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(command.amount()));

            if (account.getSavingsFund() == null) {
                account.setSavingsFund(BigDecimal.ZERO);
            }
            account.setSavingPercentage(savingPercentage);
            account.setAvailableBalance(account.getBalance().subtract(account.getSavingsFund()));

            if (savingGoal != null) {
                savingGoal.setCurrentAmount(savingGoal.getCurrentAmount().add(command.amount()));
                savingGoalRepository.save(savingGoal);
            }

        } else if (command.type() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(command.amount()));

            account.updateSavingsMetrics(savingPercentage, account.getBalance());
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

        var account = transaction.getAccount();

        // 1. Calcular la diferencia (monto nuevo - monto anterior) ANTES de modificar la entidad
        BigDecimal delta = command.amount().subtract(transaction.getAmount());

        // 2. Actualizar los datos descriptivos y de monto de la transacción
        transaction.setAmount(command.amount());
        transaction.setDescription(command.description());

        BigDecimal savingPercentage = profileRepository.findByUserId(account.getUserId())
                .map(Profile::getSaving_percentage)
                .orElse(BigDecimal.ZERO);

        // 3. Aplicar el reajuste del delta sobre el balance general
        if (transaction.getType() == TransactionType.EXPENSE) {
            // Si el gasto aumenta (delta positivo), el balance de la cuenta disminuye
            account.setBalance(account.getBalance().subtract(delta));

            account.setAvailableBalance(account.getBalance().subtract(
                    account.getSavingsFund() != null ? account.getSavingsFund() : BigDecimal.ZERO
            ));
        } else if (transaction.getType() == TransactionType.INCOME) {
            // Si el ingreso aumenta (delta positivo), el balance de la cuenta aumenta
            account.setBalance(account.getBalance().add(delta));

            // Recalcular el fondo de ahorro e indirectamente el disponible basado en el nuevo balance
            account.updateSavingsMetrics(savingPercentage, account.getBalance());
        }

        accountRepository.save(account);

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
            var transaction = transactionOptional.get();
            var account = transaction.getAccount();
            BigDecimal savingPercentage = profileRepository.findByUserId(account.getUserId())
                    .map(Profile::getSaving_percentage)
                    .orElse(BigDecimal.ZERO);

            if (transaction.getType() == TransactionType.EXPENSE) {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
                account.setAvailableBalance(account.getBalance().subtract(
                        account.getSavingsFund() != null ? account.getSavingsFund() : BigDecimal.ZERO
                ));
            } else {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
                account.updateSavingsMetrics(savingPercentage, account.getBalance());
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