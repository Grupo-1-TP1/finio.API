package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetSavingGoalByIdQuery;
import com.finio.backend.finance.domain.model.queries.GetSavingGoalsByUserIdQuery;
import com.finio.backend.finance.domain.services.SavingGoalCommandService;
import com.finio.backend.finance.domain.services.SavingGoalQueryService;
import com.finio.backend.finance.interfaces.rest.resources.CreateSavingGoalResource;
import com.finio.backend.finance.interfaces.rest.resources.SavingGoalResource;
import com.finio.backend.finance.interfaces.rest.transform.CreateSavingGoalCommandFromResourceAssembler;
import com.finio.backend.finance.interfaces.rest.transform.SavingGoalResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/saving-goals", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Saving Goals", description = "Endpoints para el control de metas y objetivos de ahorro")
public class SavingGoalController {

    private final SavingGoalCommandService savingGoalCommandService;
    private final SavingGoalQueryService savingGoalQueryService;

    public SavingGoalController(SavingGoalCommandService savingGoalCommandService, SavingGoalQueryService savingGoalQueryService) {
        this.savingGoalCommandService = savingGoalCommandService;
        this.savingGoalQueryService = savingGoalQueryService;
    }

    @PostMapping
    public ResponseEntity<SavingGoalResource> createSavingGoal(@RequestBody CreateSavingGoalResource resource) {
        var command = CreateSavingGoalCommandFromResourceAssembler.toCommandFromResource(resource);
        var savingGoal = savingGoalCommandService.handle(command);

        return savingGoal.map(value -> new ResponseEntity<>(
                SavingGoalResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{savingGoalId}")
    public ResponseEntity<SavingGoalResource> getSavingGoalById(@PathVariable Long savingGoalId) {
        var query = new GetSavingGoalByIdQuery(savingGoalId);
        var savingGoal = savingGoalQueryService.handle(query);

        return savingGoal.map(value -> ResponseEntity.ok(SavingGoalResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingGoalResource>> getSavingGoalsByUserId(@PathVariable Long userId) {
        var query = new GetSavingGoalsByUserIdQuery(userId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{savingGoalId}")
    public ResponseEntity<?> deleteSavingGoal(@PathVariable Long savingGoalId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteSavingGoalCommand(savingGoalId);
        var deleted = savingGoalCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Saving goal deleted successfully");
    }
}