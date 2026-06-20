package com.finio.backend.profiles.domain.model.commands;

import java.math.BigDecimal;

public record UpdateSavingPercentageCommand(Long userId, BigDecimal percentage) {
}
