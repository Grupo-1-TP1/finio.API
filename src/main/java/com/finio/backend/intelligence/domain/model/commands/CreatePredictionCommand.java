package com.finio.backend.intelligence.domain.model.commands;

public record CreatePredictionCommand(
        Double confidenceScore,
        Long categoryId,
        String text,
        Long transactionId
) {}
