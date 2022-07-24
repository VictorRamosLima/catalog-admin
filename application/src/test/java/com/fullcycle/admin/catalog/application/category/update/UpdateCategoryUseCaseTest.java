package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {
    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallCategoryUpdate_shouldReturnCategoryID() {
        final Category foundCategory = Category.newCategory("film", null, true);
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(foundCategory.getId(), "filme", "descrição", true);

        when(categoryGateway.findById(eq(foundCategory.getId()))).thenReturn(Optional.of(foundCategory));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final UpdateCategoryOutput output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).findById(eq(foundCategory.getId()));
        verify(categoryGateway, times(1)).update(argThat(updatedCategory -> {
            assertNotNull(updatedCategory);
            assertNotNull(updatedCategory.getId());
            assertNotNull(updatedCategory.getCreatedAt());
            assertNotNull(updatedCategory.getUpdatedAt());
            assertNull(updatedCategory.getDeletedAt());
            assertEquals("filme", updatedCategory.getName());
            assertEquals("descrição", updatedCategory.getDescription());
            assertEquals(foundCategory.getId(), updatedCategory.getId());
            assertEquals(foundCategory.getCreatedAt(), updatedCategory.getCreatedAt());
            assertTrue(updatedCategory.isActive());

            return true;
        }));
    }

    @Test
    public void givenAnInvalidName_whenCallCategoryUpdate_shouldThrowDomainException() {
        final Category foundCategory = Category.newCategory("film", null, true);
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            foundCategory.getId(),
            null,
            "descrição",
            true
        );

        when(categoryGateway.findById(eq(foundCategory.getId()))).thenReturn(Optional.of(foundCategory));

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());

        verify(categoryGateway, times(1)).findById(eq(foundCategory.getId()));
        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCategoryUpdate_shouldReturnCategoryID() {
        final Category foundCategory = Category.newCategory("film", null, true);
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            foundCategory.getId(),
            "filme",
            "descrição",
            false
        );

        when(categoryGateway.findById(eq(foundCategory.getId()))).thenReturn(Optional.of(foundCategory));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final UpdateCategoryOutput output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).findById(eq(foundCategory.getId()));
        verify(categoryGateway, times(1)).update(argThat(updatedCategory -> {
            assertNotNull(updatedCategory);
            assertNotNull(updatedCategory.getDeletedAt());
            assertFalse(updatedCategory.isActive());

            return true;
        }));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final Category foundCategory = Category.newCategory("film", null, true);
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            foundCategory.getId(),
            "filme",
            "descrição",
            true
        );

        when(categoryGateway.findById(eq(foundCategory.getId()))).thenReturn(Optional.of(foundCategory));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException("Gateway error"));

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals("Gateway error", notification.firstError().message());
        assertEquals(1, notification.getErrors().size());

        verify(categoryGateway, times(1)).findById(eq(foundCategory.getId()));
        verify(categoryGateway, times(1)).update(argThat(category -> {
            assertNotNull(category);
            assertNotNull(category.getId());
            assertNotNull(category.getCreatedAt());
            assertNotNull(category.getUpdatedAt());
            assertNull(category.getDeletedAt());
            assertEquals("filme", category.getName());
            assertEquals("descrição", category.getDescription());
            assertTrue(category.isActive());

            return true;
        }));
    }

    @Test
    public void givenAValidCommand_whenCategoryWasNotFound_shouldThrowDomainException() {
        final CategoryID categoryID = CategoryID.unique();
        final String expectedErrorMessage = "Category with id " + categoryID + " not found";
        final int expectedErrorCount = 0;
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(categoryID, null, null, false);

        when(categoryGateway.findById(eq(categoryID))).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(categoryGateway, times(1)).findById(eq(categoryID));
        verify(categoryGateway, times(0)).update(any());
    }
}
