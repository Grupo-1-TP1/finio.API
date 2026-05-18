package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.SavingGoal;
import com.finio.backend.finance.domain.model.commands.CreateSavingGoalCommand;
import com.finio.backend.finance.domain.model.commands.DeleteSavingGoalCommand;
import com.finio.backend.finance.domain.services.SavingGoalCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.SavingGoalRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SavingGoalCommandServiceImpl implements SavingGoalCommandService {

    private final SavingGoalRepository savingGoalRepository;

    public SavingGoalCommandServiceImpl(SavingGoalRepository savingGoalRepository) {
        this.savingGoalRepository = savingGoalRepository;
    }

    @Override
    public Optional<SavingGoal> handle(CreateSavingGoalCommand command) {
        try {
            SavingGoal savingGoal = new SavingGoal(command);
            return Optional.of(savingGoalRepository.save(savingGoal));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteSavingGoalCommand command) {
        if (!savingGoalRepository.existsById(command.savingGoalId())) {
            return false;
        }
        try {
            savingGoalRepository.deleteById(command.savingGoalId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
