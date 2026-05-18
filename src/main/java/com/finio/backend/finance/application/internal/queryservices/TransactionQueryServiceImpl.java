package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.TransactionQueryService;
import com.finio.backend.finance.infrastructure.persistence.jpa.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public TransactionQueryServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> handle(GetTransactionsByUserIdQuery query) {
        return transactionRepository.findByUserId(query.userId());
    }
}
