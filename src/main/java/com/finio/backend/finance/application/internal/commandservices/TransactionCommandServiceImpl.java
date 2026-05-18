package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.aggregates.TransactionType;
import com.finio.backend.finance.domain.model.commands.CreateTransactionCommand;
import com.finio.backend.finance.domain.services.TransactionCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import com.finio.backend.finance.infrastructure.persistence.jpa.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository,
                                         AccountRepository accountRepository,
                                         CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
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

        if (command.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(command.amount()));
        } else if (command.type() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(command.amount()));
        }
        accountRepository.save(account);

        Transaction transaction = new Transaction(command, account, category);
        try {
            return Optional.of(transactionRepository.save(transaction));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}