package com.fullcycle.admin.catalog.domain.validation.handler;

import com.fullcycle.admin.catalog.domain.exceptions.DomainException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification implements ValidationHandler {
    private final List<Error> errors;

    private Notification(final List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return create(new ArrayList<>());
    }

    public static Notification create(final ArrayList<Error> errors) {
        return new Notification(errors);
    }

    public static Notification create(final Error error) {
        return create().append(error);
    }

    public static Notification create(final Throwable throwable) {
        return create().append(Error.of(throwable.getMessage()));
    }

    @Override
    public Notification append(final ValidationHandler handler) {
        errors.addAll(handler.getErrors());
        return this;
    }

    @Override
    public Notification append(final Error error) {
        errors.add(error);
        return this;
    }

    @Override
    public Notification validate(final Validation validation) {
        try {
            validation.validate();
        } catch (final DomainException ex) {
            errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            append(Error.of(t.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
