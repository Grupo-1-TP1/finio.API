package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdQuery;
import java.util.List;

public interface TransactionQueryService {
    List<Transaction> handle(GetTransactionsByUserIdQuery query);
}