package com.finio.backend.intelligence.interfaces.rest;

import com.finio.backend.intelligence.domain.model.commands.DeletePredictionCommand;
import com.finio.backend.intelligence.domain.model.queries.GetPredictionByIdQuery;
import com.finio.backend.intelligence.domain.services.PredictionCommandService;
import com.finio.backend.intelligence.domain.services.PredictionQueryService;
import com.finio.backend.intelligence.interfaces.rest.resources.CreatePredictionResource;
import com.finio.backend.intelligence.interfaces.rest.resources.PredictionResource;
import com.finio.backend.intelligence.interfaces.rest.transform.CreatePredictionCommandFromResourceAssembler;
import com.finio.backend.intelligence.interfaces.rest.transform.PredictionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/predictions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Predictions", description = "Endpoints para la categorización automática de transacciones por ML")
public class PredictionController {

    private final PredictionCommandService predictionCommandService;
    private final PredictionQueryService predictionQueryService;

    public PredictionController(PredictionCommandService predictionCommandService, PredictionQueryService predictionQueryService) {
        this.predictionCommandService = predictionCommandService;
        this.predictionQueryService = predictionQueryService;
    }

    @PostMapping
    public ResponseEntity<PredictionResource> createPrediction(@RequestBody CreatePredictionResource resource) {
        var command = CreatePredictionCommandFromResourceAssembler.toCommandFromResource(resource);
        var prediction = predictionCommandService.handle(command);

        return prediction.map(value -> new ResponseEntity<>(
                PredictionResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{predictionId}")
    public ResponseEntity<PredictionResource> getPredictionById(@PathVariable Long predictionId) {
        var query = new GetPredictionByIdQuery(predictionId);
        var prediction = predictionQueryService.handle(query);

        return prediction.map(value -> ResponseEntity.ok(PredictionResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{predictionId}")
    public ResponseEntity<?> deletePrediction(@PathVariable Long predictionId) {
        var command = new DeletePredictionCommand(predictionId);
        var deleted = predictionCommandService.handle(command);

        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Prediction record deleted successfully");
    }
}
