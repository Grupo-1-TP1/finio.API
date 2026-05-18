package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.commands.CreateCategoryCommand;
import com.finio.backend.finance.domain.model.commands.DeleteCategoryCommand;

import java.util.Optional;

public interface CategoryCommandService {
    Optional<Category> handle(CreateCategoryCommand command);
    boolean handle(DeleteCategoryCommand command);
}
