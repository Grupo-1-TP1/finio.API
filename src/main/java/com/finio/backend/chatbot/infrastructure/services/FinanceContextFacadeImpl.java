package com.finio.backend.chatbot.infrastructure.services;

import com.finio.backend.chatbot.domain.services.outboundports.FinanceContextFacade;
import com.finio.backend.chatbot.interfaces.rest.resources.RecentTransactionResource;
import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.aggregates.Transaction;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.AccountQueryService;
import com.finio.backend.finance.domain.services.TransactionQueryService;
import com.finio.backend.profiles.domain.services.ProfileQueryService;
import com.finio.backend.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.finio.backend.profiles.domain.model.aggregates.Profile;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FinanceContextFacadeImpl implements FinanceContextFacade {

    private final AccountQueryService accountQueryService;
    private final TransactionQueryService transactionQueryService;
    private final ProfileQueryService profileQueryService;

    public FinanceContextFacadeImpl(AccountQueryService accountQueryService,
                                    TransactionQueryService transactionQueryService,
                                    ProfileQueryService profileQueryService) {
        this.accountQueryService = accountQueryService;
        this.transactionQueryService = transactionQueryService;
        this.profileQueryService = profileQueryService;
    }

    @Override
    public BigDecimal getUserTotalBalance(Long userId) {
        var query = new GetAccountsByUserIdQuery(userId);
        List<Account> accounts = accountQueryService.handle(query);

        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getUserSpendingByCategory(Long userId) {
        List<Transaction> transactions = transactionQueryService.handle(new GetTransactionsByUserIdQuery(userId));
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        Calendar currentCal = Calendar.getInstance();
        int currentMonth = currentCal.get(Calendar.MONTH); // 0-11
        int currentYear = currentCal.get(Calendar.YEAR);

        Calendar txCal = Calendar.getInstance();

        for (Transaction transaction : transactions) {
            if (transaction.getCreatedAt() == null) continue;

            txCal.setTime(transaction.getCreatedAt());

            if ("EXPENSE".equals(transaction.getType().toString()) &&
                    txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear) {

                String category = transaction.getCategory().getName();
                BigDecimal amount = transaction.getAmount();

                spendingByCategory.put(category,
                        spendingByCategory.getOrDefault(category, BigDecimal.ZERO).add(amount)
                );
            }
        }
        return spendingByCategory;
    }

    @Override
    public BigDecimal getUserTotalIncomeThisMonth(Long userId) {
        List<Transaction> transactions = transactionQueryService.handle(new GetTransactionsByUserIdQuery(userId));

        Calendar currentCal = Calendar.getInstance();
        int currentMonth = currentCal.get(Calendar.MONTH);
        int currentYear = currentCal.get(Calendar.YEAR);

        Calendar txCal = Calendar.getInstance();

        return transactions.stream()
                .filter(t -> t.getCreatedAt() != null && "INCOME".equals(t.getType().toString()))
                .filter(t -> {
                    txCal.setTime(t.getCreatedAt());
                    return txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear;
                })
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getUserTotalExpenseThisMonth(Long userId) {
        return getUserSpendingByCategory(userId).values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<RecentTransactionResource> getRecentTransactions(Long userId, int limit) {
        List<Transaction> transactions = transactionQueryService.handle(new GetTransactionsByUserIdQuery(userId));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return transactions.stream()
                .sorted((t1, t2) -> t2.getTransactionId().compareTo(t1.getTransactionId()))
                .limit(limit)
                .map(t -> new RecentTransactionResource(
                        t.getCreatedAt() != null ? dateFormat.format(t.getCreatedAt()) : "",
                        t.getDescription(),
                        t.getType().toString(),
                        t.getAmount(),
                        t.getCategory().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Double getUserSavingPercentage(Long userId) {
        try {
            var query = new GetProfileByUserIdQuery(userId);

            Optional<Profile> profileOptional = profileQueryService.handle(query);
            Profile profile = profileOptional.orElseThrow(() -> new RuntimeException("Profile not found"));

            BigDecimal percentage = profile.getSaving_percentage();
            return percentage != null ? percentage.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.10;
        }
    }
}