package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class UpdateCategoryUseCaseIntegrationTest {
    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway gateway;

    @Test
    public void givenAValidCommand_whenCallCategoryUpdate_shouldReturnCategoryID() {
        final Category category = Category.newCategory("film", null, true);
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            category.getId(),
            "filme",
            "descrição",
            true
        );

        final CategoryJpaEntity persistedCategory = repository.saveAndFlush(CategoryJpaEntity.from(category));

        final UpdateCategoryOutput output = useCase.execute(command).get();
        final CategoryJpaEntity categoryJpaEntity = repository.findById(category.getId().getValue()).get();

        assertNotNull(categoryJpaEntity.getId());
        assertNotNull(categoryJpaEntity.getCreatedAt());
        assertNotNull(categoryJpaEntity.getUpdatedAt());
        assertNull(categoryJpaEntity.getDeletedAt());
        assertEquals(persistedCategory.getId(), categoryJpaEntity.getId());
        assertEquals(output.id(), categoryJpaEntity.getId().toString());
        assertEquals("filme", categoryJpaEntity.getName());
        assertEquals("descrição", categoryJpaEntity.getDescription());
        assertTrue(categoryJpaEntity.isActive());
        assertTrue(categoryJpaEntity.getUpdatedAt().isAfter(persistedCategory.getCreatedAt()));
    }

    @Test
    public void givenAnInvalidName_whenCallCategoryUpdate_shouldThrowDomainException() {
        final Category foundCategory = Category.newCategory("film", null, true);
        repository.saveAndFlush(CategoryJpaEntity.from(foundCategory));

        final int expectedErrorCount = 1;
        final String expectedErrorMessage = "'name' cannot be null";
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            foundCategory.getId(),
            null,
            "descrição",
            true
        );

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals(expectedErrorMessage, notification.firstError().message());
        assertEquals(expectedErrorCount, notification.getErrors().size());

        verify(gateway, times(0)).update(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCategoryUpdate_shouldReturnCategoryID() {
        final Category category = Category.newCategory("film", null, true);
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            category.getId(),
            "filme",
            "descrição",
            false
        );

        final CategoryJpaEntity persistedCategory = repository.saveAndFlush(CategoryJpaEntity.from(category));

        final UpdateCategoryOutput output = useCase.execute(command).get();
        final CategoryJpaEntity categoryJpaEntity = repository.findById(category.getId().getValue()).get();

        assertNotNull(categoryJpaEntity.getId());
        assertNotNull(categoryJpaEntity.getCreatedAt());
        assertNotNull(categoryJpaEntity.getUpdatedAt());
        assertNotNull(categoryJpaEntity.getDeletedAt());
        assertEquals(persistedCategory.getId(), categoryJpaEntity.getId());
        assertEquals(output.id(), categoryJpaEntity.getId().toString());
        assertEquals("filme", categoryJpaEntity.getName());
        assertEquals("descrição", categoryJpaEntity.getDescription());
        assertFalse(categoryJpaEntity.isActive());
        assertTrue(categoryJpaEntity.getUpdatedAt().isAfter(persistedCategory.getCreatedAt()));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final Category category = Category.newCategory("film", null, true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            category.getId(),
            "filme",
            "descrição",
            true
        );

        doThrow(new IllegalStateException("Gateway error")).when(gateway).update(any());

        final Notification notification = useCase.execute(command).getLeft();

        assertNotNull(notification);
        assertEquals("Gateway error", notification.firstError().message());
        assertEquals(1, notification.getErrors().size());
    }

    @Test
    public void givenAValidCommand_whenCategoryWasNotFound_shouldThrowDomainException() {
        final CategoryID categoryID = CategoryID.unique();
        final String expectedErrorMessage = "Category with id " + categoryID + " not found";
        final int expectedErrorCount = 0;
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(categoryID, null, null, false);

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(command));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(gateway, times(0)).update(any());
    }
}
