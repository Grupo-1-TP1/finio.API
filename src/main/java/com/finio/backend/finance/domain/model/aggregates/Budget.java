package com.finio.backend.finance.domain.model.aggregates;

import com.finio.backend.finance.domain.model.commands.CreateBudgetCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends AuditableAbstractAggregateRoot<Budget> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal spent;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public Budget(Long userId, Category category, BigDecimal amount, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.spent = BigDecimal.ZERO;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Budget(CreateBudgetCommand command, Category category) {
        this.userId = command.userId();
        this.category = category;
        this.amount = command.amount();
        this.spent = BigDecimal.ZERO;
        this.startDate = command.startDate();
        this.endDate = command.endDate();
    }
}
