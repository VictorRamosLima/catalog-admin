package com.fullcycle.admin.catalog.domain.validation.handler;

public record Error(String message) {
    public static Error of(final String message) {
        return new Error(message);
    }
}
