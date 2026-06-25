package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.domain.model.commands.DeleteTransactionCommand;
import com.finio.backend.finance.domain.model.commands.UpdateTransactionCommand;
import com.finio.backend.finance.domain.model.events.TransactionCreatedEvent;
import com.finio.backend.finance.domain.model.valueobjects.TransactionType;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.SavingGoalRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.TransactionRepository;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCommandServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SavingGoalRepository savingGoalRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransactionCommandServiceImpl transactionCommandService;

    private Account account;
    private Category category;
    private Profile profile;
    private CreateTransactionCommand createTransactionCommand;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long accountId = 10L;
        Long categoryId = 5L;

        account = new Account(userId, "Mi billetera", BigDecimal.ZERO);
        account.setAccountId(accountId);

        category = new Category();

        profile = mock(Profile.class);
        when(profile.getSaving_percentage()).thenReturn(new BigDecimal("20.00"));

        // Command details mimicking a new income of S/. 1000.00
        createTransactionCommand = new CreateTransactionCommand(
                userId,
                accountId,
                categoryId,
                null, // savingGoalId
                TransactionType.INCOME,
                new BigDecimal("1000.00"),
                "Monthly Internship Stipend",
                LocalDate.now()
        );
    }

    @Test
    void shouldCalculateSavingsFundAndAvailableBalanceCorrectlyWhenIncomeIsRegistered() {
        // Arrange
        when(accountRepository.findById(createTransactionCommand.accountId())).thenReturn(Optional.of(account));
        when(categoryRepository.findById(createTransactionCommand.categoryId())).thenReturn(Optional.of(category));
        when(profileRepository.findByUserId(account.getUserId())).thenReturn(Optional.of(profile));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Transaction> result = transactionCommandService.handle(createTransactionCommand);

        // Assert
        assertTrue(result.isPresent());

        // Core business logic metrics verification (S/. 1000.00 income with 20% savings)
        // 1. Total balance should be 1000.00
        assertEquals(new BigDecimal("1000.00"), account.getBalance());

        // 2. Savings fund must hold exactly 20% of the total balance -> 200.00
        assertEquals(new BigDecimal("200.00"), account.getSavingsFund());

        // 3. Available balance should reflect the subtraction -> 800.00
        assertEquals(new BigDecimal("800.00"), account.getAvailableBalance());

        // Verificaciones de persistencia en la infraestructura
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldRecalculateAccountBalanceAndMetricsWhenTransactionAmountIsUpdated() {
        // Arrange
        Long transactionId = 100L;
        BigDecimal oldAmount = new BigDecimal("100.00");
        BigDecimal newAmount = new BigDecimal("150.00"); // S/. 50.00 delta increase
        BigDecimal savingPercentage = new BigDecimal("20.00");

        // 1. Setup target account with initial state (Balance=100.00)
        Account targetAccount = new Account(1L, "Main Account", oldAmount);
        targetAccount.updateSavingsMetrics(savingPercentage, oldAmount);

        // 2. Setup original transaction to be updated (INCOME type)
        Category currentCategory = new Category();
        Transaction existingTransaction = new Transaction(1L, targetAccount, currentCategory, null,
                TransactionType.INCOME, oldAmount, "Initial Income", LocalDate.now());
        existingTransaction.setTransactionId(transactionId);

        // 3. Setup update command with the new amount
        UpdateTransactionCommand updateCommand = new UpdateTransactionCommand(
                transactionId,
                5L, // categoryId
                newAmount,
                "Updated Income Description"
        );

        // Mocking repositories
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(categoryRepository.findById(updateCommand.categoryId())).thenReturn(Optional.of(currentCategory));

        // Using lenient to bypass strict stubbing conflicts with global setUp profile behavior
        lenient().when(profileRepository.findByUserId(targetAccount.getUserId())).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        // Standard clean mock save behavior
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Transaction> result = transactionCommandService.handle(updateCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(newAmount, result.get().getAmount());
        assertEquals("Updated Income Description", result.get().getDescription());

        // Pure validation of the business logic calculations inside your service:
        // 1. Total balance must reflect the old balance + delta (100.00 + 50.00) -> S/. 150.00
        assertEquals(new BigDecimal("150.00"), targetAccount.getBalance());

        // 2. Savings fund must adapt to 20% of 150.00 -> S/. 30.00
        assertEquals(new BigDecimal("30.00"), targetAccount.getSavingsFund());

        // 3. Available balance must reflect the core deduction (150.00 - 30.00) -> S/. 120.00
        assertEquals(new BigDecimal("120.00"), targetAccount.getAvailableBalance());

        // Verify repository infrastructure calls
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    @Test
    void shouldDecreaseBalanceAndRecalculateMetricsWhenIncomeTransactionIsDeleted() {
        // Arrange
        Long transactionId = 200L;
        BigDecimal incomeAmount = new BigDecimal("200.00");
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal savingPercentage = new BigDecimal("20.00");

        // 1. Setup account state assuming it already includes the S/. 200.00 income
        Account targetAccount = new Account(1L, "Main Account", initialBalance);
        targetAccount.updateSavingsMetrics(savingPercentage, initialBalance);
        // Initial: balance=1000.00, savingsFund=200.00, availableBalance=800.00

        // 2. Setup transaction to be deleted (INCOME type)
        Category currentCategory = new Category();
        Transaction incomeTransaction = new Transaction(1L, targetAccount, currentCategory, null,
                TransactionType.INCOME, incomeAmount, "Part-time Job Income", LocalDate.now());
        incomeTransaction.setTransactionId(transactionId);

        // 3. Setup delete command
        DeleteTransactionCommand deleteCommand = new DeleteTransactionCommand(transactionId);

        // Mocking repository behaviors
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(incomeTransaction));

        // Using lenient to bypass global setUp profile behavior conflicts
        lenient().when(profileRepository.findByUserId(targetAccount.getUserId())).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        // Act
        boolean isDeleted = transactionCommandService.handle(deleteCommand);

        // Assert
        assertTrue(isDeleted);

        // Mathematical Reversion Verification:
        // 1. Total balance must drop from 1000.00 to 800.00 (1000.00 - 200.00)
        assertEquals(new BigDecimal("800.00"), targetAccount.getBalance());

        // 2. Savings fund must adapt to 20% of 800.00 -> S/. 160.00
        assertEquals(new BigDecimal("160.00"), targetAccount.getSavingsFund());

        // 3. Available balance must adapt to (800.00 - 160.00) -> S/. 640.00
        assertEquals(new BigDecimal("640.00"), targetAccount.getAvailableBalance());

        // Verify infrastructure calls
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void shouldDecreaseAvailableBalanceButKeepSavingsFundIntactWhenExpenseIsRegistered() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;
        Long categoryId = 5L;
        BigDecimal expenseAmount = new BigDecimal("150.00");

        // 1. Setup account with an existing balance and savings fund already calculated
        // Initial state: Balance = S/. 1000.00, Savings (20%) = S/. 200.00, Available = S/. 800.00
        Account targetAccount = new Account(userId, "Main Account", new BigDecimal("1000.00"));
        BigDecimal savingPercentage = new BigDecimal("20.00");
        targetAccount.updateSavingsMetrics(savingPercentage, new BigDecimal("1000.00"));

        // 2. Setup mock category
        Category currentCategory = new Category();

        // 3. Setup command for an expense of S/. 150.00
        CreateTransactionCommand expenseCommand = new CreateTransactionCommand(
                userId,
                accountId,
                categoryId,
                null, // savingGoalId
                TransactionType.EXPENSE,
                expenseAmount,
                "University Textbook Purchase",
                LocalDate.now()
        );

        // Mocking repository behaviors
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(targetAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(currentCategory));

        lenient().when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Transaction> result = transactionCommandService.handle(expenseCommand);

        // Assert
        assertTrue(result.isPresent());

        // Core Business Rule Assertions:
        // 1. Total balance must decrease by the expense amount (1000.00 - 150.00) -> S/. 850.00
        assertEquals(new BigDecimal("850.00"), targetAccount.getBalance());

        // 2. CRITICAL BIZE RULE: Savings fund MUST remain completely isolated and untouched -> S/. 200.00
        assertEquals(new BigDecimal("200.00"), targetAccount.getSavingsFund());

        // 3. Available balance must reflect the new subtraction (850.00 balance - 200.00 savings) -> S/. 650.00
        assertEquals(new BigDecimal("650.00"), targetAccount.getAvailableBalance());

        // Verify correct infrastructure persistence
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldRecalculateAvailableBalanceWhenExpenseTransactionAmountIsUpdated() {
        // Arrange
        Long transactionId = 300L;
        BigDecimal oldExpenseAmount = new BigDecimal("100.00");
        BigDecimal newExpenseAmount = new BigDecimal("150.00"); // S/. 50.00 increase in expenses
        BigDecimal savingPercentage = new BigDecimal("20.00");

        // 1. Setup account post-initial income and expense
        // If initial income was 1000.00 -> balance=1000, savings=200, available=800
        // After oldExpense of 100.00 -> balance=900, savings=200, available=700
        Account targetAccount = new Account(1L, "Main Account", new BigDecimal("900.00"));
        targetAccount.setSavingsFund(new BigDecimal("200.00"));
        targetAccount.setAvailableBalance(new BigDecimal("700.00"));
        targetAccount.setSavingPercentage(savingPercentage);

        // 2. Setup original transaction to be updated (EXPENSE type)
        Category currentCategory = new Category();
        Transaction existingExpense = new Transaction(1L, targetAccount, currentCategory, null,
                TransactionType.EXPENSE, oldExpenseAmount, "University Materials", LocalDate.now());
        existingExpense.setTransactionId(transactionId);

        // 3. Setup update command with the increased expense amount
        UpdateTransactionCommand updateCommand = new UpdateTransactionCommand(
                transactionId,
                5L, // categoryId
                newExpenseAmount,
                "Almuerzo"
        );

        // Mocking repositories
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingExpense));
        when(categoryRepository.findById(updateCommand.categoryId())).thenReturn(Optional.of(currentCategory));

        lenient().when(profileRepository.findByUserId(targetAccount.getUserId())).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Transaction> result = transactionCommandService.handle(updateCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(newExpenseAmount, result.get().getAmount());

        // Mathematical Recalculation Verification:
        // 1. Total balance must drop by S/. 50.00 (900.00 - 50.00 delta) -> S/. 850.00
        assertEquals(new BigDecimal("850.00"), targetAccount.getBalance());

        // 2. Savings fund must remain isolated and unchanged -> S/. 200.00
        assertEquals(new BigDecimal("200.00"), targetAccount.getSavingsFund());

        // 3. Available balance must reflect the drop (850.00 balance - 200.00 savings) -> S/. 650.00
        assertEquals(new BigDecimal("650.00"), targetAccount.getAvailableBalance());

        // Verify infrastructure operations
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(existingExpense);
    }

    @Test
    void shouldIncreaseAvailableBalanceAndRestoreFundsWhenExpenseTransactionIsDeleted() {
        // Arrange
        Long transactionId = 400L;
        BigDecimal expenseAmount = new BigDecimal("150.00");
        BigDecimal initialBalance = new BigDecimal("850.00");
        BigDecimal savingPercentage = new BigDecimal("20.00");

        // 1. Setup account state assuming it currently has the expense deducted
        Account targetAccount = new Account(1L, "Main Account", initialBalance);
        targetAccount.setSavingsFund(new BigDecimal("200.00"));
        targetAccount.setAvailableBalance(new BigDecimal("650.00"));
        targetAccount.setSavingPercentage(savingPercentage);

        // 2. Setup transaction to be deleted (EXPENSE type)
        Category currentCategory = new Category();
        Transaction expenseTransaction = new Transaction(1L, targetAccount, currentCategory, null,
                TransactionType.EXPENSE, expenseAmount, "Incorrect Book Charge", LocalDate.now());
        expenseTransaction.setTransactionId(transactionId);

        // 3. Setup delete command
        DeleteTransactionCommand deleteCommand = new DeleteTransactionCommand(transactionId);

        // Mocking repository behaviors
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(expenseTransaction));

        lenient().when(profileRepository.findByUserId(targetAccount.getUserId())).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        // Act
        boolean isDeleted = transactionCommandService.handle(deleteCommand);

        // Assert
        assertTrue(isDeleted);

        // Reversion and Restitution Verification:
        // 1. Total balance must absorb the expense back (850.00 + 150.00) -> S/. 1000.00
        assertEquals(new BigDecimal("1000.00"), targetAccount.getBalance());

        // 2. Savings fund must remain completely untouched -> S/. 200.00
        assertEquals(new BigDecimal("200.00"), targetAccount.getSavingsFund());

        // 3. Available balance must absorb the money back (1000.00 balance - 200.00 savings) -> S/. 800.00
        assertEquals(new BigDecimal("800.00"), targetAccount.getAvailableBalance());

        // Verify repository infrastructure triggers
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void shouldPublishTransactionCreatedEventWhenExpenseApproachesOrExceedsBudgetThreshold() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;
        Long categoryId = 5L;
        BigDecimal criticalExpenseAmount = new BigDecimal("90.00"); // Represents 90% of a S/. 100.00 budget
        BigDecimal savingPercentage = new BigDecimal("0.00");

        // 1. Setup account state
        Account targetAccount = new Account(userId, "Main Student Account", new BigDecimal("500.00"));
        targetAccount.setAccountId(accountId);
        targetAccount.updateSavingsMetrics(savingPercentage, new BigDecimal("500.00"));

        // 2. Setup mock category
        Category currentCategory = new Category();
        currentCategory.setCategoryId(categoryId);

        // 3. Setup command for the critical expense
        CreateTransactionCommand criticalExpenseCommand = new CreateTransactionCommand(
                userId,
                accountId,
                categoryId,
                null, // savingGoalId
                TransactionType.EXPENSE,
                criticalExpenseAmount,
                "High Category Expense",
                LocalDate.now()
        );

        // Mocking repository behaviors
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(targetAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(currentCategory));

        lenient().when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        lenient().when(profile.getSaving_percentage()).thenReturn(savingPercentage);

        // Return transaction on save
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Transaction> result = transactionCommandService.handle(criticalExpenseCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(criticalExpenseAmount, result.get().getAmount());

        // CRITICAL THRESHOLD VERIFICATION:
        // Verify that Spring's ApplicationEventPublisher was successfully triggered to publish the alert event
        verify(eventPublisher, times(1)).publishEvent(any(TransactionCreatedEvent.class));

        // Verify infrastructure updates were successfully executed
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}