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
                entity.getBalance(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    public static AccountResource toResourceFromEntity(Account entity, BigDecimal savingPercentage) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        BigDecimal percentage = (savingPercentage != null) ? savingPercentage : BigDecimal.valueOf(0.0);

        return new AccountResource(
                entity.getAccountId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBalance(),
                entity.getAvailableBalance(percentage),
                entity.calculateSavingsFund(percentage),
                percentage
        );
    }
}
