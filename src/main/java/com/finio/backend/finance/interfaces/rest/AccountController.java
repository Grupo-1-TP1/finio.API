package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.aggregates.Account;
import com.finio.backend.finance.domain.model.queries.GetAccountByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import com.finio.backend.finance.domain.services.AccountCommandService;
import com.finio.backend.finance.domain.services.AccountQueryService;
import com.finio.backend.finance.interfaces.rest.resources.AccountResource;
import com.finio.backend.finance.interfaces.rest.resources.CreateAccountResource;
import com.finio.backend.finance.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import com.finio.backend.finance.interfaces.rest.transform.CreateAccountCommandFromResourceAssembler;
import com.finio.backend.profiles.domain.model.aggregates.Profile;
import com.finio.backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Endpoints para la gestión de cuentas y billeteras financieras")
public class AccountController {

    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;
    private final ProfileRepository profileRepository;

    public AccountController(AccountCommandService accountCommandService, AccountQueryService accountQueryService, ProfileRepository profileRepository) {
        this.accountCommandService = accountCommandService;
        this.accountQueryService = accountQueryService;
        this.profileRepository = profileRepository;
    }

    @PostMapping
    public ResponseEntity<AccountResource> createAccount(@RequestBody CreateAccountResource resource) {
        var command = CreateAccountCommandFromResourceAssembler.toCommandFromResource(resource);
        var account = accountCommandService.handle(command);

        return account.map(value -> new ResponseEntity<>(
                AccountResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResource> getAccountById(@PathVariable Long accountId) {
        var query = new GetAccountByIdQuery(accountId);
        var accountOptional = accountQueryService.handle(query);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountOptional.get();

        BigDecimal savingPercentage = profileRepository.findByUserId(account.getUserId())
                .map(Profile::getSaving_percentage).orElse(BigDecimal.valueOf(0.0));

        AccountResource resource = AccountResourceFromEntityAssembler.toResourceFromEntity(account, savingPercentage);

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResource>> getAccountsByUserId(@PathVariable Long userId) {
        BigDecimal savingPercentage = profileRepository.findByUserId(userId)
                .map(Profile::getSaving_percentage)
                .orElse(BigDecimal.valueOf(0.0));

        var query = new GetAccountsByUserIdQuery(userId);
        var accounts = accountQueryService.handle(query);

        var resources = accounts.stream()
                .map(account -> AccountResourceFromEntityAssembler.toResourceFromEntity(account, savingPercentage))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteAccountCommand(accountId);
        var deleted = accountCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Account deleted successfully");
    }
}
