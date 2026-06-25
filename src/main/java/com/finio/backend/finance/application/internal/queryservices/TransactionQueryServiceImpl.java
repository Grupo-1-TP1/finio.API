package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.queries.GetTransactionsBySavingGoalIdQuery;
import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdAndMonthAndYear;
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

    @Override
    public List<Transaction> handle(GetTransactionsBySavingGoalIdQuery query) {
        return transactionRepository.findBySavingGoal_SavingGoalId(query.savingGoalId());
    }

    @Override
    public List<Transaction> handle(GetTransactionsByUserIdAndMonthAndYear query) {
        return transactionRepository.findByUserIdAndMonthAndYear(query.userId(), query.month(), query.year());
    }
}
