package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {
    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallCategoryCreation_shouldReturnCategoryID() {
        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", true);
        when(categoryGateway.create(any())).thenAnswer(returnsFirstArg());

        final CreateCategoryOutput output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).create(argThat(category -> {
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
    public void givenAnInvalidName_whenCallCategoryCreation_shouldThrowDomainException() {
        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";
        final CreateCategoryCommand command = CreateCategoryCommand.from(null, "descrição", true);

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCategoryCreation_shouldReturnCategoryID() {
        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", false);
        when(categoryGateway.create(any())).thenAnswer(returnsFirstArg());

        final CreateCategoryOutput output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).create(argThat(category -> {
            assertNotNull(category);
            assertNotNull(category.getId());
            assertNotNull(category.getCreatedAt());
            assertNotNull(category.getUpdatedAt());
            assertNotNull(category.getDeletedAt());
            assertEquals("filme", category.getName());
            assertEquals("descrição", category.getDescription());
            assertFalse(category.isActive());

            return true;
        }));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", true);
        when(categoryGateway.create(any())).thenThrow(new IllegalStateException("Gateway error"));

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals("Gateway error", notification.firstError().message());
        assertEquals(1, notification.getErrors().size());

        verify(categoryGateway, times(1)).create(argThat(category -> {
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
}
