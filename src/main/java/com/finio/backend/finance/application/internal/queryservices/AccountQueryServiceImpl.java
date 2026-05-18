package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.queries.GetAccountByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import com.finio.backend.finance.domain.services.AccountQueryService;
import com.finio.backend.finance.infrastructure.persistence.jpa.AccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> handle(GetAccountByIdQuery query) {
        return accountRepository.findById(query.accountId());
    }

    @Override
    public List<Account> handle(GetAccountsByUserIdQuery query) {
        return accountRepository.findByUserId(query.userId());
    }
}
