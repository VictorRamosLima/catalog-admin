package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;

import java.util.UUID;

public record CreateCategoryOutput(UUID id) {
    public static CreateCategoryOutput from(final CategoryID categoryId) {
        return new CreateCategoryOutput(categoryId.getValue());
    }

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId().getValue());
    }
}
