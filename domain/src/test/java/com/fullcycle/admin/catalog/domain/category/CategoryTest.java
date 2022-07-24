package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.StringHelper;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.fullcycle.admin.catalog.domain.StringHelper.generateRandomString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryTest {
    @Test
    public void givenValidParams_whenCallNewCategory_thenShouldReturnNewInstanceOfCategory() {
        final Category category = Category.newCategory("Filme", "descrição", true);

        assertNotNull(category);
        assertNotNull(category.getId());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNull(category.getDeletedAt());
        assertEquals("Filme", category.getName());
        assertEquals("descrição", category.getDescription());
        assertTrue(category.isActive());
    }

    @Test
    public void givenNullName_whenCallNewCategoryAndValidate_thenShouldReturnError() {
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";

        final Category category = Category.newCategory(null, "descrição", true);

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> category.validate(new ThrowsValidationHandler())
        );

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenEmptyName_whenCallNewCategoryAndValidate_thenShouldReturnError() {
        final String emptyName = "   ";
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be empty";

        final Category category = Category.newCategory(emptyName, "descrição", true);

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> category.validate(new ThrowsValidationHandler())
        );

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenNameWithSizeLessThan3_whenCallNewCategoryAndValidate_thenShouldReturnError() {
        final String invalidName = "Fi   ";

        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' must be between 3 and 255 characters";

        final Category category = Category.newCategory(invalidName, "descrição", true);

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> category.validate(new ThrowsValidationHandler())
        );

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenNameWithSizeAboveThan255_whenCallNewCategoryAndValidate_thenShouldReturnError() {
        final String name = generateRandomString(256);

        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' must be between 3 and 255 characters";

        final Category category = Category.newCategory(name, "descrição", true);

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> category.validate(new ThrowsValidationHandler())
        );

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenEmptyDescription_whenCallNewCategoryAndValidate_thenShouldNotReturnErrors() {
        final Category category = Category.newCategory("Filme", "", true);
        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
    }

    @Test
    public void givenIsActiveFalse_whenCallNewCategoryAndValidate_thenShouldNotReturnErrors() {
        final Category category = Category.newCategory("Filme", "Descrição", false);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertNotNull(category.getDeletedAt());
    }

    @Test
    public void givenAValidActiveCategory_whenCallDeactivate_shouldReturnCategoryInactive() {
        final Category category = Category.newCategory("Filme", "Descrição", true);

        assertNull(category.getDeletedAt());
        assertTrue(category.isActive());
        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Category deactivatedCategory = category.deactivate();

        assertNotNull(deactivatedCategory);
        assertNotNull(deactivatedCategory.getUpdatedAt());
        assertNotNull(deactivatedCategory.getDeletedAt());
        assertEquals(category.getName(), deactivatedCategory.getName());
        assertEquals(category.getDescription(), deactivatedCategory.getDescription());
        assertEquals(category.getId(), deactivatedCategory.getId());
        assertEquals(category.getCreatedAt(), deactivatedCategory.getCreatedAt());
        assertTrue(deactivatedCategory.getUpdatedAt().isAfter(category.getUpdatedAt()));
        assertFalse(deactivatedCategory.isActive());
    }

    @Test
    public void givenAValidInactiveCategory_whenCallActivate_shouldReturnCategoryActive() {
        final Category category = Category.newCategory("Filme", "Descrição", false);

        assertNotNull(category.getDeletedAt());
        assertFalse(category.isActive());
        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Category activeCategory = category.activate();

        assertNotNull(activeCategory);
        assertNotNull(activeCategory.getId());
        assertNotNull(activeCategory.getCreatedAt());
        assertNotNull(activeCategory.getUpdatedAt());
        assertNull(activeCategory.getDeletedAt());
        assertEquals(category.getName(), activeCategory.getName());
        assertEquals(category.getDescription(), activeCategory.getDescription());
        assertEquals(category.getId(), activeCategory.getId());
        assertEquals(category.getCreatedAt(), activeCategory.getCreatedAt());
        assertTrue(activeCategory.isActive());
    }

    @Test
    public void givenAValidCategory_whenCallUpdate_shouldReturnUpdatedCategory() {
        final Category category = Category.newCategory("Filme", "Descrição", true);

        assertNull(category.getDeletedAt());
        assertTrue(category.isActive());
        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Category updatedCategory = category.update("Novo Filme", "Nova Descrição", false);

        assertNotNull(updatedCategory);
        assertNotNull(updatedCategory.getId());
        assertNotNull(updatedCategory.getCreatedAt());
        assertNotNull(updatedCategory.getUpdatedAt());
        assertNotNull(updatedCategory.getDeletedAt());
        assertEquals("Novo Filme", updatedCategory.getName());
        assertEquals("Nova Descrição", updatedCategory.getDescription());
        assertEquals(category.getId(), updatedCategory.getId());
        assertEquals(category.getCreatedAt(), updatedCategory.getCreatedAt());
        assertFalse(updatedCategory.isActive());
    }

    @Test
    public void givenAValidCategory_whenCallUpdateWithInvalidParams_shouldNotThrowAnException() {
        final Category category = Category.newCategory("Filme", "Descrição", true);

        assertNull(category.getDeletedAt());
        assertTrue(category.isActive());
        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertDoesNotThrow(() -> category.update(null, "Nova Descrição", false));
    }
}
