package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetBudgetByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetBudgetsByUserIdQuery;
import com.finio.backend.finance.domain.services.BudgetCommandService;
import com.finio.backend.finance.domain.services.BudgetQueryService;
import com.finio.backend.finance.interfaces.rest.resources.BudgetResource;
import com.finio.backend.finance.interfaces.rest.resources.CreateBudgetResource;
import com.finio.backend.finance.interfaces.rest.transform.BudgetResourceFromEntityAssembler;
import com.finio.backend.finance.interfaces.rest.transform.CreateBudgetCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Budgets", description = "Endpoints para la gestión de presupuestos límites por categoría")
public class BudgetController {

    private final BudgetCommandService budgetCommandService;
    private final BudgetQueryService budgetQueryService;

    public BudgetController(BudgetCommandService budgetCommandService, BudgetQueryService budgetQueryService) {
        this.budgetCommandService = budgetCommandService;
        this.budgetQueryService = budgetQueryService;
    }

    @PostMapping
    public ResponseEntity<BudgetResource> createBudget(@RequestBody CreateBudgetResource resource) {
        var command = CreateBudgetCommandFromResourceAssembler.toCommandFromResource(resource);
        var budget = budgetCommandService.handle(command);

        return budget.map(value -> new ResponseEntity<>(
                BudgetResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResource> getBudgetById(@PathVariable Long budgetId) {
        var query = new GetBudgetByIdQuery(budgetId);
        var budget = budgetQueryService.handle(query);

        return budget.map(value -> ResponseEntity.ok(BudgetResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetResource>> getBudgetsByUserId(@PathVariable Long userId) {
        var query = new GetBudgetsByUserIdQuery(userId);
        var budgets = budgetQueryService.handle(query);

        var resources = budgets.stream()
                .map(BudgetResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteBudgetCommand(budgetId);
        var deleted = budgetCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Budget deleted successfully");
    }
}
