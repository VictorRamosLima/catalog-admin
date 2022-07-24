package com.fullcycle.admin.catalog.infrastructure.category.persistence;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.PersistenceTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@PersistenceTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenANullName_whenCallCreate_shouldThrowException() {
        final Category category = Category.newCategory("filmes", "description", true);
        final CategoryJpaEntity categoryJpaEntity = CategoryJpaEntity.from(category);
        categoryJpaEntity.setName(null);

        final DataIntegrityViolationException exception = assertThrows(
            DataIntegrityViolationException.class,
            () -> categoryRepository.save(categoryJpaEntity)
        );

        final PropertyValueException cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals("name", cause.getPropertyName());
    }

    @Test
    public void givenANullUpdatedAt_whenCallCreate_shouldThrowException() {
        final Category category = Category.newCategory("filmes", "description", true);
        final CategoryJpaEntity categoryJpaEntity = CategoryJpaEntity.from(category);
        categoryJpaEntity.setUpdatedAt(null);

        final DataIntegrityViolationException exception = assertThrows(
            DataIntegrityViolationException.class,
            () -> categoryRepository.save(categoryJpaEntity)
        );

        final PropertyValueException cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals("updatedAt", cause.getPropertyName());
    }

    @Test
    public void givenANullCreatedAt_whenCallCreate_shouldThrowException() {
        final Category category = Category.newCategory("filmes", "description", true);
        final CategoryJpaEntity categoryJpaEntity = CategoryJpaEntity.from(category);
        categoryJpaEntity.setCreatedAt(null);

        final DataIntegrityViolationException exception = assertThrows(
            DataIntegrityViolationException.class,
            () -> categoryRepository.save(categoryJpaEntity)
        );

        final PropertyValueException cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals("createdAt", cause.getPropertyName());
    }
}
