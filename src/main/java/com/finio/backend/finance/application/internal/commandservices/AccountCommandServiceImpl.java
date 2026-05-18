package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.commands.CreateAccountCommand;
import com.finio.backend.finance.domain.services.AccountCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;

    public AccountCommandServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> handle(CreateAccountCommand command) {
        Account account = new Account(command);
        try {
            return Optional.of(accountRepository.save(account));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
