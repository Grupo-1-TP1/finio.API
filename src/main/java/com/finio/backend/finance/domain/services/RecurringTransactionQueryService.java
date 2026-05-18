package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionQueryService {
    Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query);
    List<RecurringTransaction> handle(GetRecurringTransactionsByUserIdQuery query);
}