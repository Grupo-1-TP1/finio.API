package com.finio.backend.finance.application.internal.eventhandlers;

import com.finio.backend.finance.domain.model.events.BudgetLimitReachedEvent;
import com.finio.backend.finance.domain.model.events.TransactionCreatedEvent;
import com.finio.backend.finance.infrastructure.persistence.jpa.BudgetRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TransactionEventHandler {

    private final BudgetRepository budgetRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Inyectamos el repositorio que acabamos de configurar
    public TransactionEventHandler(BudgetRepository budgetRepository, ApplicationEventPublisher eventPublisher) {
        this.budgetRepository = budgetRepository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    @Transactional
    public void on(TransactionCreatedEvent event) {
        if (!"EXPENSE".equals(event.type())) return;

        LocalDate today = LocalDate.now();
        var budgetOptional = budgetRepository.findByUserIdAndCategory_CategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                event.userId(), event.categoryId(), today, today
        );

        budgetOptional.ifPresent(budget -> {
            BigDecimal previousSpent = budget.getSpent();
            budget.increaseSpent(event.amount());
            budgetRepository.save(budget);

            // Reglas de límites
            BigDecimal limit80 = budget.getAmount().multiply(new BigDecimal("0.80"));
            BigDecimal limit100 = budget.getAmount();
            String categoryName = budget.getCategory().getName();

            // CASO A: Superó el 100%
            if (budget.getSpent().compareTo(limit100) >= 0 && previousSpent.compareTo(limit100) < 0) {
                eventPublisher.publishEvent(new BudgetLimitReachedEvent(
                        event.userId(), categoryName, budget.getAmount(), budget.getSpent(), "EXCEEDED_100"
                ));
            }
            // CASO B: Superó el 80%
            else if (budget.getSpent().compareTo(limit80) >= 0 && previousSpent.compareTo(limit80) < 0) {
                eventPublisher.publishEvent(new BudgetLimitReachedEvent(
                        event.userId(), categoryName, budget.getAmount(), budget.getSpent(), "WARNING_80"
                ));
            }
        });
    }
}