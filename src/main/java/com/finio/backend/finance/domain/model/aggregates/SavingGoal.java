package com.finio.backend.finance.domain.model.aggregates;

import com.finio.backend.finance.domain.model.commands.CreateSavingGoalCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "saving_goals")
@Getter
@Setter
@NoArgsConstructor
public class SavingGoal extends AuditableAbstractAggregateRoot<SavingGoal> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saving_goal_id")
    private Long savingGoalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentAmount;

    @Column(nullable = false)
    private LocalDate deadline;

    public SavingGoal(Long userId, String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
    }

    public SavingGoal(CreateSavingGoalCommand command) {
        this.userId = command.userId();
        this.name = command.name();
        this.targetAmount = command.targetAmount();
        this.currentAmount = command.currentAmount() != null ? command.currentAmount() : BigDecimal.ZERO;
        this.deadline = command.deadline();
    }
}