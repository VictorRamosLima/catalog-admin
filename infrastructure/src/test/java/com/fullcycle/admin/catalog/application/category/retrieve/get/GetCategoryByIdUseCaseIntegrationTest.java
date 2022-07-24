package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class GetCategoryByIdUseCaseIntegrationTest {
    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway gateway;

    @Test
    public void givenValidId_whenCallGetCategory_shouldReturnCategory() {
        assertEquals(0, repository.count());

        final Category category = Category.newCategory("filme", "descrição", true);
        final CategoryID id = category.getId();

        repository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, repository.count());

        final CategoryOutput output = useCase.execute(id.toString());

        assertNotNull(output);
        assertEquals(category.getId(), output.id());
        assertEquals(category.getName(), output.name());
        assertEquals(category.getDescription(), output.description());
        assertEquals(category.isActive(), output.isActive());

        final CategoryJpaEntity foundCategory = repository.findById(id.getValue()).get();

        assertEquals(foundCategory.getId(), output.id().getValue());
        assertEquals(foundCategory.getName(), output.name());
        assertEquals(foundCategory.getDescription(), output.description());
        assertEquals(foundCategory.isActive(), output.isActive());
        assertEquals(foundCategory.getCreatedAt(), output.createdAt());
        assertEquals(foundCategory.getUpdatedAt(), output.updatedAt());
        assertEquals(foundCategory.getDeletedAt(), output.deletedAt());
    }

    @Test
    public void givenValidId_whenCallGetCategoryThatDoesNotExist_shouldReturnNotFound() {
        final CategoryID id = CategoryID.unique();
        final int expectedErrorCount = 0;
        final String expectedErrorMessage = "Category with id " + id + " not found";

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(id.toString()));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());
    }

    @Test
    public void givenInvalidId_whenCallGetCategory_shouldReturnNotFound() {
        final CategoryID id = CategoryID.unique();
        final int expectedErrorCount = 0;
        final String expectedErrorMessage = "Category with id " + id + "z not found";

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> useCase.execute(id + "z"));

        assertEquals(expectedErrorMessage, exception.getMessage());
        assertEquals(expectedErrorCount, exception.getErrors().size());

        verify(gateway, times(0)).findById(any());
    }

    @Test
    public void givenValidId_whenGatewayThrowsAnException_shouldThrowException() {
        final CategoryID id = CategoryID.unique();

        doThrow(new IllegalStateException("Gateway error")).when(gateway).findById(id);

        final IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(id.toString())
        );

        assertEquals("Gateway error", exception.getMessage());
    }
}
