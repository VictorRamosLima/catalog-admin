package com.fullcycle.admin.catalog.domain.exceptions;

import com.fullcycle.admin.catalog.domain.validation.handler.Error;

import java.util.List;

public class DomainException extends NoStackTraceException {
    private final List<Error> errors;

    protected DomainException(final List<Error> errors) {
        super("One or more errors occurred during validation");
        this.errors = errors;
    }

    protected DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors;
    }

    public static DomainException from(final List<Error> errors) {
        return new DomainException(errors);
    }

    public static DomainException from(final String message, final List<Error> errors) {
        return new DomainException(message, errors);
    }

    public static DomainException from(Error error) {
        return new DomainException(List.of(error));
    }

    public static DomainException from(final String message, Error error) {
        return new DomainException(message, List.of(error));
    }

    public List<Error> getErrors() {
        return errors;
    }
}
