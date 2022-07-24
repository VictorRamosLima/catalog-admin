package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class GenreID extends Identifier {
    private final UUID value;

    private GenreID(final UUID value) {
        Objects.requireNonNull(value, "'value' must not be null");
        this.value = value;
    }

    public static GenreID unique() {
        return new GenreID(UUID.randomUUID());
    }

    public static GenreID from(final String value) {
        return new GenreID(UUID.fromString(value));
    }

    public static GenreID from(final UUID value) {
        return new GenreID(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreID that = (GenreID) o;
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
