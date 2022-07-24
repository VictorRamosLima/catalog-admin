package com.fullcycle.admin.catalog.application.category.retrieve.list;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {
    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenValidQuery_whenCallListCategories_shouldReturnCategories() {
        final CategorySearchQuery query = new CategorySearchQuery(0, 10, "", "createdAt", "desc");
        final List<Category> categories = List.of(
            Category.newCategory("filme", "descrição", true),
            Category.newCategory("série", "descrição", true),
            Category.newCategory("desenho", "descrição", true)
        );
        final Pagination<Category> pagination = new Pagination<>(0, 10, 3, categories);

        final int expectedItemsCount = 3;
        final Pagination<CategoryListOutput> expectedResult = pagination.map(CategoryListOutput::from);

        when(categoryGateway.findAll(eq(query))).thenReturn(pagination);

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedResult, result);
        assertEquals(0, result.currentPage());
        assertEquals(10, result.perPage());
        assertEquals(3, result.total());

        verify(categoryGateway, times(1)).findAll(eq(query));
    }

    @Test
    public void givenValidQuery_whenHasNoResult_shouldReturnEmptyCategories() {
        final CategorySearchQuery query = new CategorySearchQuery(0, 10, "", "createdAt", "desc");
        final Pagination<Category> pagination = new Pagination<>(0, 10, 0, Collections.emptyList());

        final int expectedItemsCount = 0;
        final Pagination<CategoryListOutput> expectedResult = pagination.map(CategoryListOutput::from);

        when(categoryGateway.findAll(eq(query))).thenReturn(pagination);

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedResult, result);
        assertEquals(0, result.currentPage());
        assertEquals(10, result.perPage());
        assertEquals(0, result.total());

        verify(categoryGateway, times(1)).findAll(eq(query));
    }

    @Test
    public void givenValidQuery_whenGatewayThrowsAnException_shouldThrowException() {
        final CategorySearchQuery query = new CategorySearchQuery(0, 10, "", "createdAt", "desc");

        when(categoryGateway.findAll(eq(query))).thenThrow(new IllegalStateException("Gateway error"));

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> useCase.execute(query));

        assertNotNull(exception);
        assertEquals("Gateway error", exception.getMessage());

        verify(categoryGateway, times(1)).findAll(eq(query));
    }
}
