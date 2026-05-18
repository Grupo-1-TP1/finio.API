package com.finio.backend.finance.application.internal.commandservices;

import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.commands.CreateCategoryCommand;
import com.finio.backend.finance.domain.services.CategoryCommandService;
import com.finio.backend.finance.infrastructure.persistence.jpa.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;

    public CategoryCommandServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> handle(CreateCategoryCommand command) {
        Category category = new Category(command);
        try {
            return Optional.of(categoryRepository.save(category));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
