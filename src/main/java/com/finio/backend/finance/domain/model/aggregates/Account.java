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

    @Column(name = "available_balance", precision = 12, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "savings_fund", precision = 12, scale = 2)
    private BigDecimal savingsFund;

    @Column(name = "saving_percentage", precision = 5, scale = 2)
    private BigDecimal savingPercentage;

    public Account(Long userId, String name, BigDecimal balance) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.savingPercentage = BigDecimal.ZERO;
        this.savingsFund = BigDecimal.ZERO;
        this.availableBalance = balance;
    }

    public Account(CreateAccountCommand createAccountCommand) {
        this.userId = createAccountCommand.userId();
        this.name = createAccountCommand.name();
        this.balance = createAccountCommand.balance();
        this.savingPercentage = BigDecimal.ZERO;
        this.savingsFund = BigDecimal.ZERO;
        this.availableBalance = createAccountCommand.balance();
    }

    public void updateSavingsMetrics(BigDecimal currentPercentage, BigDecimal baseIncomeAmount) {
        this.savingPercentage = (currentPercentage != null) ? currentPercentage : BigDecimal.ZERO;

        if (this.savingPercentage.compareTo(BigDecimal.ZERO) <= 0 || baseIncomeAmount == null) {
            this.savingsFund = BigDecimal.ZERO;
        } else {
            this.savingsFund = baseIncomeAmount
                    .multiply(this.savingPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        // El disponible es simplemente la sustracción lineal libre de mutaciones dinámicas
        this.availableBalance = this.balance.subtract(this.savingsFund);
    }
}
