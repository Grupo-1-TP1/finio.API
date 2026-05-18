package com.finio.backend.finance.domain.model.commands;

import java.math.BigDecimal;

public record CreateAccountCommand(
        Long userId,
        String name,
        BigDecimal balance
) {}
