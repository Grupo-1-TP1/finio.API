package com.finio.backend.finance.interfaces.rest.transform;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.interfaces.rest.resources.AccountResource;

public class AccountResourceFromEntityAssembler {
    public static AccountResource toResourceFromEntity(Account entity) {
        return new AccountResource(
                entity.getAccountId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBalance()
        );
    }
}
