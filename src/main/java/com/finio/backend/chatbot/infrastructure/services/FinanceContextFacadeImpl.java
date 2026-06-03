package com.finio.backend.chatbot.infrastructure.services;

import com.finio.backend.chatbot.domain.services.outboundports.FinanceContextFacade;
import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.AccountQueryService;
import com.finio.backend.finance.domain.services.TransactionQueryService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceContextFacadeImpl implements FinanceContextFacade {

    private final AccountQueryService accountQueryService;
    private final TransactionQueryService transactionQueryService;

    public FinanceContextFacadeImpl(AccountQueryService accountQueryService, TransactionQueryService transactionQueryService) {
        this.accountQueryService = accountQueryService;
        this.transactionQueryService = transactionQueryService;
    }

    @Override
    public BigDecimal getUserTotalBalance(Long userId) {
        // 1. Instanciamos tu query de cuentas para el usuario
        var query = new GetAccountsByUserIdQuery(userId);
        List<Account> accounts = accountQueryService.handle(query);

        // 2. Sumamos los saldos de todas sus cuentas usando Streams de Java
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getUserSpendingByCategory(Long userId) {
        // 1. Instanciamos tu query de transacciones
        var query = new GetTransactionsByUserIdQuery(userId);
        List<Transaction> transactions = transactionQueryService.handle(query);

        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        // 2. Iteramos las transacciones y filtramos estrictamente los EGRESOS / GASTOS
        for (Transaction transaction : transactions) {
            // Ajusta "EXPENSE" o "GASTO" según cómo manejes el tipo en tu Enum o String de Transaction
            if ("EXPENSE".equals(transaction.getType().toString())) {

                String category = transaction.getCategory().getName(); // Ej: "Comida", "Transporte"
                BigDecimal amount = transaction.getAmount();

                // Vamos acumulando el monto gastado por cada categoría encontrada
                spendingByCategory.put(category,
                        spendingByCategory.getOrDefault(category, BigDecimal.ZERO).add(amount)
                );
            }
        }

        return spendingByCategory;
    }
}