package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.validation.handler.ValidationHandler;

import java.time.Instant;

public class Category extends AggregateRoot<CategoryID> {
    private final String name;
    private final String description;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Instant deletedAt;

    private Category(
        final CategoryID id,
        final String name,
        final String description,
        final boolean isActive,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        super(id);
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Category newCategory(final String name, final String description, final boolean isActive) {
        final Instant now = Instant.now();
        final Instant deletedAt = isActive ? null : now;
        return new Category(CategoryID.unique(), name, description, isActive, now, now, deletedAt);
    }

    public static Category from(
        final CategoryID id,
        final String name,
        final String description,
        final boolean isActive,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        return new Category(id, name, description, isActive, createdAt, updatedAt, deletedAt);
    }

    public CategoryID getId() {
        return id;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CategoryValidator(handler, this).validate();
    }

    public Category deactivate() {
        final Instant now = Instant.now();
        final Instant deletionDate = deletedAt == null ? now : deletedAt;
        return new Category(id, name, description, false, createdAt, now, deletionDate);
    }

    public Category activate() {
        final Instant now = Instant.now();
        return new Category(id, name, description, true, createdAt, now, null);
    }

    public Category update(final String name, final String description, final boolean isActive) {
        final Instant now = Instant.now();
        final Instant deletionDate = isActive ? null : (deletedAt == null ? now : deletedAt);
        return new Category(id, name, description, isActive, createdAt, now, deletionDate);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
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
