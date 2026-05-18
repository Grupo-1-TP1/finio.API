package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetRecurringTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.RecurringTransactionCommandService;
import com.finio.backend.finance.domain.services.RecurringTransactionQueryService;
import com.finio.backend.finance.interfaces.rest.resources.CreateRecurringTransactionResource;
import com.finio.backend.finance.interfaces.rest.resources.RecurringTransactionResource;
import com.finio.backend.finance.interfaces.rest.transform.CreateRecurringTransactionCommandFromResourceAssembler;
import com.finio.backend.finance.interfaces.rest.transform.RecurringTransactionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/recurring-transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Recurring Transactions", description = "Endpoints para el registro de transacciones frecuentes o programadas")
public class RecurringTransactionController {

    private final RecurringTransactionCommandService recurringTransactionCommandService;
    private final RecurringTransactionQueryService recurringTransactionQueryService;

    public RecurringTransactionController(RecurringTransactionCommandService recurringTransactionCommandService,
                                          RecurringTransactionQueryService recurringTransactionQueryService) {
        this.recurringTransactionCommandService = recurringTransactionCommandService;
        this.recurringTransactionQueryService = recurringTransactionQueryService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionResource> createRecurringTransaction(@RequestBody CreateRecurringTransactionResource resource) {
        var command = CreateRecurringTransactionCommandFromResourceAssembler.toCommandFromResource(resource);
        var recurringTransaction = recurringTransactionCommandService.handle(command);

        return recurringTransaction.map(value -> new ResponseEntity<>(
                RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{recurringTransactionId}")
    public ResponseEntity<RecurringTransactionResource> getRecurringTransactionById(@PathVariable Long recurringTransactionId) {
        var query = new GetRecurringTransactionByIdQuery(recurringTransactionId);
        var recurringTransaction = recurringTransactionQueryService.handle(query);

        return recurringTransaction.map(value -> ResponseEntity.ok(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecurringTransactionResource>> getRecurringTransactionsByUserId(@PathVariable Long userId) {
        var query = new GetRecurringTransactionsByUserIdQuery(userId);
        var recurringTransactions = recurringTransactionQueryService.handle(query);

        var resources = recurringTransactions.stream()
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{recurringTransactionId}")
    public ResponseEntity<?> deleteRecurringTransaction(@PathVariable Long recurringTransactionId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteRecurringTransactionCommand(recurringTransactionId);
        var deleted = recurringTransactionCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Recurring transaction deleted successfully");
    }
}
