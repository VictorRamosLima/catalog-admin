package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.UUID;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {
    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway, "'categoryGateway' is required");
    }

    @Override
    public CategoryOutput execute(String id) {
        if (isValidUUID(id)) {
            return categoryGateway.findById(CategoryID.from(id))
                .map(CategoryOutput::from)
                .orElseThrow(() -> notFoundException(id));
        }

        throw notFoundException(id);
    }

    private NotFoundException notFoundException(String id) {
        return NotFoundException.from(Category.class, id);
    }

    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
