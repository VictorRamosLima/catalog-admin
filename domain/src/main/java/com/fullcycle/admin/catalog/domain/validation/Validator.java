package com.fullcycle.admin.catalog.domain.validation;

import com.fullcycle.admin.catalog.domain.validation.handler.ValidationHandler;

public abstract class Validator {
    private final ValidationHandler handler;

    public Validator(final ValidationHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();

    protected ValidationHandler getValidationHandler() {
        return handler;
    }
}
