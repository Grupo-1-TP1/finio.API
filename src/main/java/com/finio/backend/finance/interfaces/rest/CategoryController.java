package com.finio.backend.finance.interfaces.rest;

import com.finio.backend.finance.domain.model.queries.GetAllCategoriesQuery;
import com.finio.backend.finance.domain.model.queries.GetCategoryByIdQuery;
import com.finio.backend.finance.domain.services.CategoryCommandService;
import com.finio.backend.finance.domain.services.CategoryQueryService;
import com.finio.backend.finance.interfaces.rest.resources.CategoryResource;
import com.finio.backend.finance.interfaces.rest.resources.CreateCategoryResource;
import com.finio.backend.finance.interfaces.rest.transform.CategoryResourceFromEntityAssembler;
import com.finio.backend.finance.interfaces.rest.transform.CreateCategoryCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Categories", description = "Endpoints para el catálogo maestro de categorías financieras")
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    public CategoryController(CategoryCommandService categoryCommandService, CategoryQueryService categoryQueryService) {
        this.categoryCommandService = categoryCommandService;
        this.categoryQueryService = categoryQueryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResource> createCategory(@RequestBody CreateCategoryResource resource) {
        var command = CreateCategoryCommandFromResourceAssembler.toCommandFromResource(resource);
        var category = categoryCommandService.handle(command);

        return category.map(value -> new ResponseEntity<>(
                CategoryResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResource> getCategoryById(@PathVariable Long categoryId) {
        var query = new GetCategoryByIdQuery(categoryId);
        var category = categoryQueryService.handle(query);

        return category.map(value -> ResponseEntity.ok(CategoryResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResource>> getAllCategories() {
        var query = new GetAllCategoriesQuery();
        var categories = categoryQueryService.handle(query);

        var resources = categories.stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        var command = new com.finio.backend.finance.domain.model.commands.DeleteCategoryCommand(categoryId);
        var deleted = categoryCommandService.handle(command);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Category deleted successfully");
    }
}