package com.finio.backend.intelligence.interfaces.rest.resources;

public record CreatePredictionResource(
        Double confidenceScore,
        Long categoryId,
        String text,
        Long transactionId
) {}
