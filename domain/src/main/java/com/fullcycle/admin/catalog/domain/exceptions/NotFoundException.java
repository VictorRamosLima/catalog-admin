package com.fullcycle.admin.catalog.domain.exceptions;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.validation.handler.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(final String message, final List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException from(final Class<? extends AggregateRoot<?>> aggregate, final String id) {
        final String message = String.format("%s with id %s not found", aggregate.getSimpleName(), id);
        return new NotFoundException(message, Collections.emptyList());
    }
}
