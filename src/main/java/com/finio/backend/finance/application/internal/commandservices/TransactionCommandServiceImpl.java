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

        // 1. CONTROL DE INTEGRIDAD: Validación de la existencia de la cuenta y la categoría
        Optional<Account> accountOptional = accountRepository.findById(command.accountId());
        Optional<Category> categoryOptional = categoryRepository.findById(command.categoryId());

        if (accountOptional.isEmpty() || categoryOptional.isEmpty()) {
            return Optional.empty(); // Aborta el flujo si faltan llaves foráneas válidas
        }

        Account account = accountOptional.get();
        Category category = categoryOptional.get();
        SavingGoal savingGoal = null;

        // 2. EXTRACCIÓN DE ENTORNO: Obtención de la tasa de ahorro preconfigurada en el perfil
        BigDecimal savingPercentage = profileRepository.findByUserId(account.getUserId())
                .map(Profile::getSaving_percentage)
                .orElse(BigDecimal.ZERO);

        // 3. LÓGICA DE NEGOCIO SECTORIAL: Evaluación según el tipo de movimiento
        if (command.type() == TransactionType.EXPENSE) {
            // Regla aritmética: Los gastos disminuyen el balance general de la cuenta
            account.setBalance(account.getBalance().subtract(command.amount()));

            // Inicialización preventiva del fondo de ahorro local
            if (account.getSavingsFund() == null) {
                account.setSavingsFund(BigDecimal.ZERO);
            }
            account.setSavingPercentage(savingPercentage);

            // Aislamiento: El saldo disponible es el balance total menos el fondo protegido
            account.setAvailableBalance(account.getBalance().subtract(account.getSavingsFund()));

            // Actualización del progreso si el gasto está asociado a una meta material específica
            if (savingGoal != null) {
                savingGoal.setCurrentAmount(savingGoal.getCurrentAmount().add(command.amount()));
                savingGoalRepository.save(savingGoal);
            }

        } else if (command.type() == TransactionType.INCOME) {
            // Regla aritmética: Los ingresos aumentan el balance general de la cuenta
            account.setBalance(account.getBalance().add(command.amount()));

            // Recálculo automático de métricas de ahorro basado en la nueva liquidez
            account.updateSavingsMetrics(savingPercentage, account.getBalance());
        }

        // Persistencia del estado actualizado de los balances en la cuenta
        accountRepository.save(account);

        // 4. PERSISTENCIA Y ARQUITECTURA DIRIGIDA POR EVENTOS
        Transaction transaction = new Transaction(command, account, category, savingGoal);
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);

            // GATILLO DE NOTIFICACIÓN: Si es un gasto, se publica el evento para evaluar alertas push
            if (savedTransaction.getType() == TransactionType.EXPENSE) {
                var event = new TransactionCreatedEvent(
                        account.getUserId(),
                        category.getCategoryId(),
                        savedTransaction.getType().name(),
                        savedTransaction.getAmount(),
                        savedTransaction.getDescription(),
                        savedTransaction.getTransactionDate()
                );
                // Envío asíncrono hacia el módulo encargado de verificar el umbral del presupuesto
                eventPublisher.publishEvent(event);
            }

            return Optional.of(savedTransaction);
        } catch (Exception e) {
            return Optional.empty(); // Captura excepciones de base de datos de manera segura
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