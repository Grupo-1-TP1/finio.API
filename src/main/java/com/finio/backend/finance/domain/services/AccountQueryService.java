package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.queries.GetAccountByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface AccountQueryService {
    Optional<Account> handle(GetAccountByIdQuery query);
    List<Account> handle(GetAccountsByUserIdQuery query);
}
