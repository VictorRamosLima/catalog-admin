package com.fullcycle.admin.catalog.application.category.delete;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
public class DeleteCategoryUseCaseIntegrationTest {
    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @SpyBean
    private CategoryGateway gateway;

    @Test
    public void givenValidId_whenCallCategoryDeletion_shouldBeOK() {
        final Category category = Category.newCategory("filme", "descrição", true);
        repository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, repository.count());
        assertDoesNotThrow(() -> useCase.execute(category.getId().toString()));
        assertEquals(0, repository.count());
    }

    @Test
    public void givenInvalidId_whenCallCategoryDeletion_shouldBeOK() {
        assertEquals(0, repository.count());
        assertDoesNotThrow(() -> useCase.execute(CategoryID.unique() + "invalid"));
        assertEquals(0, repository.count());
    }

    @Test
    public void givenValidId_whenGatewayThrowsAnError_shouldReturnException() {
        final CategoryID id = CategoryID.unique();

        doThrow(new IllegalStateException("Gateway error")).when(gateway).deleteById(eq(id));

        assertThrows(IllegalStateException.class, () -> useCase.execute(id.toString()));
    }
}
