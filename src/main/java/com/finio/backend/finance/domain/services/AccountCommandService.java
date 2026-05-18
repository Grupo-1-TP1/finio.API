package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.commands.CreateAccountCommand;
import com.finio.backend.finance.domain.model.commands.DeleteAccountCommand;

import java.util.Optional;

public interface AccountCommandService {

    Optional<Account> handle(CreateAccountCommand command);
    boolean handle(DeleteAccountCommand command);
}