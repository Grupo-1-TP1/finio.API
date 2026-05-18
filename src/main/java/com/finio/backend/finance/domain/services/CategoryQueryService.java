package com.finio.backend.finance.domain.services;

import com.finio.backend.finance.domain.model.aggregates.Category;
import com.finio.backend.finance.domain.model.queries.GetAllCategoriesQuery;
import com.finio.backend.finance.domain.model.queries.GetCategoryByIdQuery;
import java.util.List;
import java.util.Optional;

public interface CategoryQueryService {
    Optional<Category> handle(GetCategoryByIdQuery query);
    List<Category> handle(GetAllCategoriesQuery query);
}
