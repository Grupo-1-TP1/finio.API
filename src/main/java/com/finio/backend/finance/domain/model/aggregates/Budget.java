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

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    public Budget(Long userId, Category category, BigDecimal amount, LocalDate date) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.spent = BigDecimal.ZERO;
        this.month = date.getMonthValue();
        this.year = date.getYear();
    }

    public Budget(CreateBudgetCommand command, Category category) {
        this.userId = command.userId();
        this.category = category;
        this.amount = command.amount();
        this.spent = BigDecimal.ZERO;
        this.date = LocalDate.now();
        this.month = date.getMonthValue();
        this.year = date.getYear();
    }

    public void increaseSpent(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The spent amount must be positive");
        }
        this.spent = this.spent.add(amount);
    }
}
