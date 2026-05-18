package com.finio.backend.intelligence.interfaces.rest;

import com.finio.backend.intelligence.domain.model.commands.DeleteRecommendationCommand;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationByIdQuery;
import com.finio.backend.intelligence.domain.model.queries.GetRecommendationsByUserIdQuery;
import com.finio.backend.intelligence.domain.services.RecommendationCommandService;
import com.finio.backend.intelligence.domain.services.RecommendationQueryService;
import com.finio.backend.intelligence.interfaces.rest.resources.CreateRecommendationResource;
import com.finio.backend.intelligence.interfaces.rest.resources.RecommendationResource;
import com.finio.backend.intelligence.interfaces.rest.transform.CreateRecommendationCommandFromResourceAssembler;
import com.finio.backend.intelligence.interfaces.rest.transform.RecommendationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/recommendations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Recommendations", description = "Endpoints para las sugerencias de presupuestos inteligentes")
public class RecommendationController {

    private final RecommendationCommandService recommendationCommandService;
    private final RecommendationQueryService recommendationQueryService;

    public RecommendationController(RecommendationCommandService recommendationCommandService, RecommendationQueryService recommendationQueryService) {
        this.recommendationCommandService = recommendationCommandService;
        this.recommendationQueryService = recommendationQueryService;
    }

    @PostMapping
    public ResponseEntity<RecommendationResource> createRecommendation(@RequestBody CreateRecommendationResource resource) {
        var command = CreateRecommendationCommandFromResourceAssembler.toCommandFromResource(resource);
        var recommendation = recommendationCommandService.handle(command);

        return recommendation.map(value -> new ResponseEntity<>(
                RecommendationResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<RecommendationResource> getRecommendationById(@PathVariable Long recommendationId) {
        var query = new GetRecommendationByIdQuery(recommendationId);
        var recommendation = recommendationQueryService.handle(query);

        return recommendation.map(value -> ResponseEntity.ok(RecommendationResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationResource>> getRecommendationsByUserId(@PathVariable Long userId) {
        var query = new GetRecommendationsByUserIdQuery(userId);
        var recommendations = recommendationQueryService.handle(query);

        var resources = recommendations.stream()
                .map(RecommendationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<?> deleteRecommendation(@PathVariable Long recommendationId) {
        var command = new DeleteRecommendationCommand(recommendationId);
        var deleted = recommendationCommandService.handle(command);

        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Recommendation and its details deleted successfully");
    }
}
