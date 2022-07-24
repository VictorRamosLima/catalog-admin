package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class CategoryID extends Identifier {
    private final UUID value;

    private CategoryID(final UUID value) {
        Objects.requireNonNull(value, "'value' must not be null");
        this.value = value;
    }

    public static CategoryID unique() {
        return new CategoryID(UUID.randomUUID());
    }

    public static CategoryID from(final String value) {
        return new CategoryID(UUID.fromString(value));
    }

    public static CategoryID from(final UUID value) {
        return new CategoryID(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CategoryID that = (CategoryID) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
