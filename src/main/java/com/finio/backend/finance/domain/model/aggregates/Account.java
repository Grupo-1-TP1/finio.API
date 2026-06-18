package com.finio.backend.finance.domain.model.aggregates;

import com.finio.backend.finance.domain.model.commands.CreateAccountCommand;
import com.finio.backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AuditableAbstractAggregateRoot<Account> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    public Account(Long userId, String name, BigDecimal balance) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
    }

    public Account(CreateAccountCommand createAccountCommand) {
        this.userId = createAccountCommand.userId();
        this.name = createAccountCommand.name();
        this.balance = createAccountCommand.balance();
    }

    public BigDecimal calculateSavingsFund(BigDecimal savingPercentage) {
        if (savingPercentage == null || savingPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return this.balance
                .multiply(savingPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAvailableBalance(BigDecimal savingPercentage) {
        BigDecimal savingsFund = this.calculateSavingsFund(savingPercentage);
        return this.balance.subtract(savingsFund);
    }
}
