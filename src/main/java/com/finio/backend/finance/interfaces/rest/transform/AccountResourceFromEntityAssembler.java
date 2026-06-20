package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.interfaces.rest.resources.AccountResource;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public class AccountResourceFromEntityAssembler {
    public static AccountResource toResourceFromEntity(Account entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        return new AccountResource(
                entity.getAccountId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBalance(),
                entity.getAvailableBalance() != null ? entity.getAvailableBalance() : entity.getBalance(),
                entity.getSavingsFund() != null ? entity.getSavingsFund() : BigDecimal.ZERO,
                entity.getSavingPercentage() != null ? entity.getSavingPercentage() : BigDecimal.ZERO
        );
    }

    public static AccountResource toResourceFromEntity(Account entity, BigDecimal savingPercentage) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        BigDecimal percentage = (entity.getSavingPercentage() != null && entity.getSavingPercentage().compareTo(BigDecimal.ZERO) > 0)
                ? entity.getSavingPercentage()
                : ((savingPercentage != null) ? savingPercentage : BigDecimal.ZERO);

        return new AccountResource(
                entity.getAccountId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBalance(),
                entity.getAvailableBalance() != null ? entity.getAvailableBalance() : entity.getBalance(),
                entity.getSavingsFund() != null ? entity.getSavingsFund() : BigDecimal.ZERO,
                percentage
        );
    }
}
