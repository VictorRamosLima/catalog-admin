package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.validation.handler.ValidationHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genre extends AggregateRoot<GenreID> {
    private String name;
    private boolean isActive;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(final GenreID id) {
        super(id);
    }

    protected Genre(
        final GenreID id,
        final String name,
        final boolean isActive,
        final List<CategoryID> categories,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        this(id);
        this.name = name;
        this.isActive = isActive;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Genre newGenre(final String name, final boolean isActive) {
        final Instant now = Instant.now();
        final Instant deletedAt = isActive ? null : now;
        return new Genre(GenreID.unique(), name, isActive, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(
        final GenreID id,
        final String name,
        final boolean isActive,
        final List<CategoryID> categories,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        return new Genre(id, name, isActive, categories, createdAt, updatedAt, deletedAt);
    }

    public static Genre with(final Genre genre) {
        return new Genre(
            genre.getId(),
            genre.getName(),
            genre.isActive(),
            genre.getCategories(),
            genre.getCreatedAt(),
            genre.getUpdatedAt(),
            genre.getDeletedAt()
        );
    }

    @Override
    public void validate(ValidationHandler handler) {
        new GenreValidator(handler, this).validate();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
