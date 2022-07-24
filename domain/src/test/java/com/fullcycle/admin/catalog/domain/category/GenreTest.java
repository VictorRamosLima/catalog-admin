package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static com.fullcycle.admin.catalog.domain.StringHelper.generateRandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenreTest {
    @Test
    public void givenValidParams_whenCallNewGenre_shouldReturnCreatedGenre() {
        final int expectedCategoriesSize = 0;
        final Genre genre = Genre.newGenre("action", true);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
        assertEquals(expectedCategoriesSize, genre.getCategories().size());
        assertEquals("action", genre.getName());
        assertTrue(genre.isActive());
    }

    @Test
    public void givenNullName_whenCallNewGenre_shouldReturnError() {
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";
        final Genre genre = Genre.newGenre(null, true);

        final DomainException exception = assertThrows(DomainException.class, () -> {
            genre.validate(new ThrowsValidationHandler());
        });

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenEmptyName_whenCallNewGenre_shouldReturnError() {
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be empty";
        final Genre genre = Genre.newGenre(null, true);

        final DomainException exception = assertThrows(DomainException.class, () -> {
            genre.validate(new ThrowsValidationHandler());
        });

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenNameWithLengthGreaterThan255_whenCallNewGenre_shouldReturnError() {
        final String name = generateRandomString(256);
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' length must be between 1 and 255";
        final Genre genre = Genre.newGenre(name, true);

        final DomainException exception = assertThrows(DomainException.class, () -> {
            genre.validate(new ThrowsValidationHandler());
        });

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }
}
