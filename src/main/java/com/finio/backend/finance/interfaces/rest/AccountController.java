package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetAccountByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetAccountsByUserIdQuery;
import com.finio.backend.finance.domain.services.AccountCommandService;
import com.finio.backend.finance.domain.services.AccountQueryService;
import com.finio.backend.finance.interfaces.rest.resources.AccountResource;
import com.finio.backend.finance.interfaces.rest.resources.CreateAccountResource;
import com.finio.backend.finance.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import com.finio.backend.finance.interfaces.rest.transform.CreateAccountCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Endpoints para la gestión de cuentas y billeteras financieras")
public class AccountController {

    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;

    public AccountController(AccountCommandService accountCommandService, AccountQueryService accountQueryService) {
        this.accountCommandService = accountCommandService;
        this.accountQueryService = accountQueryService;
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
        var account = accountQueryService.handle(query);

        return account.map(value -> ResponseEntity.ok(AccountResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResource>> getAccountsByUserId(@PathVariable Long userId) {
        var query = new GetAccountsByUserIdQuery(userId);
        var accounts = accountQueryService.handle(query);

        var resources = accounts.stream()
                .map(AccountResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}
