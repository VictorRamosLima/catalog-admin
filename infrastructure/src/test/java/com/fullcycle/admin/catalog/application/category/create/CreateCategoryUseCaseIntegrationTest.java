package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class CreateCategoryUseCaseIntegrationTest {
    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway gateway;

    @Test
    public void givenAValidCommand_whenCallCategoryCreation_shouldReturnCategoryID() {
        assertEquals(0, repository.count());

        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", true);

        final CreateCategoryOutput output = useCase.execute(command).get();
        final UUID categoryId = output.id();

        assertEquals(1, repository.count());

        assertNotNull(output);
        assertNotNull(categoryId);

        final CategoryJpaEntity createdCategory = repository.findById(categoryId).get();

        assertNotNull(createdCategory.getId());
        assertNotNull(createdCategory.getCreatedAt());
        assertNotNull(createdCategory.getUpdatedAt());
        assertNull(createdCategory.getDeletedAt());
        assertEquals(categoryId, createdCategory.getId());
        assertEquals("filme", createdCategory.getName());
        assertEquals("descrição", createdCategory.getDescription());
        assertTrue(createdCategory.isActive());
    }

    @Test
    public void givenAnInvalidName_whenCallCategoryCreation_shouldThrowDomainException() {
        assertEquals(0, repository.count());

        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";
        final CreateCategoryCommand command = CreateCategoryCommand.from(null, "descrição", true);

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(0, repository.count());

        verify(gateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCategoryCreation_shouldReturnCategoryID() {
        assertEquals(0, repository.count());
        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", false);

        final CreateCategoryOutput output = useCase.execute(command).get();
        final UUID categoryId = output.id();

        assertEquals(1, repository.count());

        assertNotNull(output);
        assertNotNull(categoryId);

        final CategoryJpaEntity createdCategory = repository.findById(categoryId).get();

        assertNotNull(createdCategory.getId());
        assertNotNull(createdCategory.getCreatedAt());
        assertNotNull(createdCategory.getUpdatedAt());
        assertNotNull(createdCategory.getDeletedAt());
        assertEquals(categoryId, createdCategory.getId());
        assertEquals("filme", createdCategory.getName());
        assertEquals("descrição", createdCategory.getDescription());
        assertFalse(createdCategory.isActive());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final CreateCategoryCommand command = CreateCategoryCommand.from("filme", "descrição", true);
        doThrow(new IllegalStateException("Gateway error")).when(gateway).create(any());

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals("Gateway error", notification.firstError().message());
        assertEquals(1, notification.getErrors().size());
    }
}
