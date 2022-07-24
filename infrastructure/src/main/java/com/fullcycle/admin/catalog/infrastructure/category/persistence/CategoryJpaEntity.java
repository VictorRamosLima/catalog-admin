package com.fullcycle.admin.catalog.infrastructure.category.persistence;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "category")
public class CategoryJpaEntity {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE NOT NULL")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE NOT NULL")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant deletedAt;

    public CategoryJpaEntity() {}

    private CategoryJpaEntity(
        final UUID id,
        final String name,
        final String description,
        final boolean isActive,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        this.id = Objects.requireNonNull(id, "'id' cannot be null");
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' cannot be null");
        this.deletedAt = deletedAt;
    }

    public static CategoryJpaEntity from(final Category category) {
        return new CategoryJpaEntity(
            category.getId().getValue(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getCreatedAt(),
            category.getUpdatedAt(),
            category.getDeletedAt()
        );
    }

    public Category toDomain() {
        return Category.from(
            CategoryID.from(getId()),
            getName(),
            getDescription(),
            isActive(),
            getCreatedAt(),
            getUpdatedAt(),
            getDeletedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
