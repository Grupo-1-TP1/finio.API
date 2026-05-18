package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.RecurringTransaction;
import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.RecurringTransactionQueryService;
import com.finio.backend.finance.infrastructure.persistence.jpa.RecurringTransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionQueryServiceImpl implements RecurringTransactionQueryService {

    private final RecurringTransactionRepository recurringTransactionRepository;

    public RecurringTransactionQueryServiceImpl(RecurringTransactionRepository recurringTransactionRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @Override
    public Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query) {
        return recurringTransactionRepository.findById(query.recurringTransactionId());
    }

    @Override
    public List<RecurringTransaction> handle(GetRecurringTransactionsByUserIdQuery query) {
        return recurringTransactionRepository.findByUserId(query.userId());
    }
}
