package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetTransactionsByUserIdQuery;
import com.finio.backend.finance.domain.services.TransactionCommandService;
import com.finio.backend.finance.domain.services.TransactionQueryService;
import com.finio.backend.finance.interfaces.rest.resources.CreateTransactionResource;
import com.finio.backend.finance.interfaces.rest.resources.TransactionResource;
import com.finio.backend.finance.interfaces.rest.transform.CreateTransactionCommandFromResourceAssembler;
import com.finio.backend.finance.interfaces.rest.transform.TransactionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints para el registro e historial de movimientos financieros")
public class TransactionController {

    private final TransactionCommandService transactionCommandService;
    private final TransactionQueryService transactionQueryService;

    public TransactionController(TransactionCommandService transactionCommandService, TransactionQueryService transactionQueryService) {
        this.transactionCommandService = transactionCommandService;
        this.transactionQueryService = transactionQueryService;
    }

    @PostMapping
    public ResponseEntity<TransactionResource> createTransaction(@RequestBody CreateTransactionResource resource) {
        var command = CreateTransactionCommandFromResourceAssembler.toCommandFromResource(resource);
        var transaction = transactionCommandService.handle(command);

        return transaction.map(value -> new ResponseEntity<>(
                TransactionResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResource>> getTransactionsByUserId(@PathVariable Long userId) {
        var query = new GetTransactionsByUserIdQuery(userId);
        var transactions = transactionQueryService.handle(query);

        var resources = transactions.stream()
                .map(TransactionResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long transactionId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteTransactionCommand(transactionId);
        var deleted = transactionCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Transaction deleted and account balance updated successfully");
    }
}
