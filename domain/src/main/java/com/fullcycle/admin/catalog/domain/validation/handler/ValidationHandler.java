package com.fullcycle.admin.catalog.domain.validation.handler;

import java.util.List;

public interface ValidationHandler {
    ValidationHandler append(ValidationHandler handler);
    ValidationHandler append(Error error);
    ValidationHandler validate(Validation validation);
    List<Error> getErrors();
    default boolean hasErrors() {
        return getErrors() != null && !getErrors().isEmpty();
    }
    default Error firstError() {
        if (hasErrors()) {
            return getErrors().get(0);
        }

        return null;
    }
}
