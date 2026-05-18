package com.finio.backend.finance.application.internal.queryservices;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.queries.GetSavingGoalByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetSavingGoalsByUserIdQuery;
import com.finio.backend.finance.domain.services.SavingGoalQueryService;
import com.finio.backend.finance.infrastructure.persistence.jpa.SavingGoalRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SavingGoalQueryServiceImpl implements SavingGoalQueryService {

    private final SavingGoalRepository savingGoalRepository;

    public SavingGoalQueryServiceImpl(SavingGoalRepository savingGoalRepository) {
        this.savingGoalRepository = savingGoalRepository;
    }

    @Override
    public Optional<SavingGoal> handle(GetSavingGoalByIdQuery query) {
        return savingGoalRepository.findById(query.savingGoalId());
    }

    @Override
    public List<SavingGoal> handle(GetSavingGoalsByUserIdQuery query) {
        return savingGoalRepository.findByUserId(query.userId());
    }
}