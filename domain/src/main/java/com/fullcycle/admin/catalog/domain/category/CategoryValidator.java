package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.validation.Validator;
import com.fullcycle.admin.catalog.domain.validation.handler.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.ValidationHandler;

public class CategoryValidator extends Validator {
    private static final int CATEGORY_NAME_MIN_LENGTH = 3;
    private static final int CATEGORY_NAME_MAX_LENGTH = 255;
    private final Category category;

    public CategoryValidator(final ValidationHandler handler, final Category category) {
        super(handler);
        this.category = category;
    }

    @Override
    public void validate() {
        checkNameConstraints();
    }

    private void checkNameConstraints() {
        final String categoryName = category.getName();
        if (categoryName == null) {
            getValidationHandler().append(Error.of("'name' cannot be null"));
            return;
        }

        if (categoryName.isBlank()) {
            getValidationHandler().append(Error.of("'name' cannot be empty"));
            return;
        }

        final int categoryNameLength = categoryName.trim().length();
        if (categoryNameLength < CATEGORY_NAME_MIN_LENGTH || categoryNameLength > CATEGORY_NAME_MAX_LENGTH) {
            getValidationHandler().append(Error.of("'name' must be between 3 and 255 characters"));
        }
    }
}
