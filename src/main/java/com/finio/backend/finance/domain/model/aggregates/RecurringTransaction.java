package com.finio.backend.finance.domain.model.aggregates;

import com.finio.backend.finance.domain.model.commands.CreateRecurringTransactionCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction extends AuditableAbstractAggregateRoot<RecurringTransaction> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurring_transaction_id")
    private Long recurringTransactionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 30)
    private String frequency;

    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;

    public RecurringTransaction(CreateRecurringTransactionCommand command, Account account, Category category) {
        this.userId = command.userId();
        this.account = account;
        this.category = category;
        this.type = command.type();
        this.amount = command.amount();
        this.description = command.description();
        this.frequency = command.frequency();
        this.nextExecutionDate = command.nextExecutionDate();
    }
}
