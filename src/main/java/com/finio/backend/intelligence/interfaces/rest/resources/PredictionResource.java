package com.finio.backend.intelligence.interfaces.rest.resources;

public record PredictionResource(
        Long id,
        Double confidenceScore,
        Long categoryId,
        Long transactionId
) {}
