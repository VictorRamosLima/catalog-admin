package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseTest {
    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenValidId_whenCallGetCategory_shouldReturnCategory() {
        final Category category = Category.newCategory("filme", "descrição", true);
        final CategoryID id = category.getId();

        when(categoryGateway.findById(id)).thenReturn(Optional.of(category));

        final CategoryOutput output = useCase.execute(id.toString());

        assertNotNull(output);
        assertEquals(category.getId(), output.id());
        assertEquals(category.getName(), output.name());
        assertEquals(category.getDescription(), output.description());
        assertEquals(category.isActive(), output.isActive());
        assertEquals(category.getCreatedAt(), output.createdAt());
        assertEquals(category.getUpdatedAt(), output.updatedAt());
        assertEquals(category.getDeletedAt(), output.deletedAt());

        verify(categoryGateway, times(1)).findById(id);
    }

    @Test
    public void givenValidId_whenCallGetCategoryThatDoesNotExist_shouldReturnNotFound() {
        final CategoryID id = CategoryID.unique();
        final int expectedErrorCount = 0;
        final String expectedErrorMessage = "Category with id " + id + " not found";

        when(categoryGateway.findById(id)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(id.toString()));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(categoryGateway, times(1)).findById(id);
    }

    @Test
    public void givenInvalidId_whenCallGetCategory_shouldReturnNotFound() {
        final CategoryID id = CategoryID.unique();
        final int expectedErrorCount = 0;
        final String expectedErrorMessage = "Category with id " + id + "z not found";

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(id + "z"));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(categoryGateway, times(0)).findById(any());
    }

    @Test
    public void givenValidId_whenGatewayThrowsAnException_shouldThrowException() {
        final CategoryID id = CategoryID.unique();

        when(categoryGateway.findById(id)).thenThrow(new IllegalStateException("Gateway error"));

        final IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(id.toString())
        );

        assertEquals("Gateway error", exception.getMessage());

        verify(categoryGateway, times(1)).findById(id);
    }
}
