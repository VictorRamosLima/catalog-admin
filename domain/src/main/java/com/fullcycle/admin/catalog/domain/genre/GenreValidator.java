package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.validation.Validator;
import com.fullcycle.admin.catalog.domain.validation.handler.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.ValidationHandler;

public class GenreValidator  extends Validator {
    private static final int CATEGORY_NAME_MIN_LENGTH = 1;
    private static final int CATEGORY_NAME_MAX_LENGTH = 255;
    private final Genre genre;

    public GenreValidator(final ValidationHandler handler, final Genre genre) {
        super(handler);
        this.genre = genre;
    }

    @Override
    public void validate() {
        checkNameConstraints();
    }

    private void checkNameConstraints() {
        final String genreName = this.genre.getName();
        if (genreName == null) {
            getValidationHandler().append(Error.of("'name' cannot be null"));
            return;
        }

        if (genreName.isBlank()) {
            getValidationHandler().append(Error.of("'name' cannot be empty"));
            return;
        }

        final int categoryNameLength = genreName.trim().length();
        if (categoryNameLength < CATEGORY_NAME_MIN_LENGTH || categoryNameLength > CATEGORY_NAME_MAX_LENGTH) {
            getValidationHandler().append(Error.of("'name' must be between 1 and 255 characters"));
        }
    }
}
