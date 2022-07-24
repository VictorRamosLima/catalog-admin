package com.fullcycle.admin.catalog.infrastructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.PostgreSQLGatewayTest;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PostgreSQLGatewayTest
public class CategoryPostgreSQLGatewayTest {
    @Autowired
    private CategoryPostgreSQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallCreate_shouldReturnNewCategory() {
        final Category category = Category.newCategory("filmes", "filmes", true);

        assertEquals(0, categoryRepository.count());

        final Category createdCategory = categoryGateway.create(category);

        assertNotNull(createdCategory);
        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), createdCategory.getId());
        assertEquals(category.getName(), createdCategory.getName());
        assertEquals(category.getDescription(), createdCategory.getDescription());
        assertEquals(category.isActive(), createdCategory.isActive());
        assertEquals(category.getCreatedAt(), createdCategory.getCreatedAt());
        assertEquals(category.getUpdatedAt(), createdCategory.getUpdatedAt());
        assertEquals(category.getDeletedAt(), createdCategory.getDeletedAt());

        final CategoryJpaEntity foundCategory = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(category.getId().getValue(), foundCategory.getId());
        assertEquals(category.getName(), foundCategory.getName());
        assertEquals(category.getDescription(), foundCategory.getDescription());
        assertEquals(category.isActive(), foundCategory.isActive());
        assertEquals(category.getCreatedAt(), foundCategory.getCreatedAt());
        assertEquals(category.getUpdatedAt(), foundCategory.getUpdatedAt());
        assertEquals(category.getDeletedAt(), foundCategory.getDeletedAt());
    }

    @Test
    public void givenAPersistedCategory_whenCallDelete_shouldDeleteCategory() {
        final Category category = Category.newCategory("filme", "filme", true);
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category)).toDomain();

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(category.getId());
        final Optional<CategoryJpaEntity> foundCategory = categoryRepository.findById(category.getId().getValue());

        assertTrue(foundCategory.isEmpty());
        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenANotPersistedCategory_whenCallDelete_shouldDeleteCategory() {
        assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.unique());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPersistedCategoryAndAValidCategoryID_whenCallFindById_shouldReturnCategory() {
        assertEquals(0, categoryRepository.count());

        final Category category = Category.newCategory("filme", "filme", true);
        final Category createdCategory = categoryRepository.saveAndFlush(CategoryJpaEntity.from(category)).toDomain();

        assertEquals(1, categoryRepository.count());

        final Optional<Category> foundCategory = categoryGateway.findById(createdCategory.getId());

        assertFalse(foundCategory.isEmpty());
        assertEquals(createdCategory.getId(), foundCategory.get().getId());
        assertEquals(createdCategory.getName(), foundCategory.get().getName());
        assertEquals(createdCategory.getDescription(), foundCategory.get().getDescription());
        assertEquals(createdCategory.isActive(), foundCategory.get().isActive());
        assertEquals(createdCategory.getCreatedAt(), foundCategory.get().getCreatedAt());
        assertEquals(createdCategory.getUpdatedAt(), foundCategory.get().getUpdatedAt());
        assertEquals(createdCategory.getDeletedAt(), foundCategory.get().getDeletedAt());
        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void givenANotPersistedCategoryAndAValidCategoryID_whenCallFindById_shouldReturnEmpty() {
        assertEquals(0, categoryRepository.count());

        final Optional<Category> foundCategory = categoryGateway.findById(CategoryID.unique());

        assertTrue(foundCategory.isEmpty());
        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAValidCategory_whenCallUpdate_shouldReturnUpdatedCategory() {
        final Category category = Category.newCategory("film", "", true);
        final Category createdCategory = categoryRepository.saveAndFlush(CategoryJpaEntity.from(category)).toDomain();

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), createdCategory.getId());
        assertEquals(category.getName(), createdCategory.getName());
        assertEquals(category.getDescription(), createdCategory.getDescription());

        final Category categoryToUpdate = category.update("filmes", "filmes", true);
        final Category updatedCategory = categoryGateway.update(categoryToUpdate);

        assertNotNull(updatedCategory);
        assertTrue(category.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt()));
        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), updatedCategory.getId());
        assertEquals("filmes", updatedCategory.getName());
        assertEquals("filmes", updatedCategory.getDescription());
        assertEquals(category.isActive(), updatedCategory.isActive());
        assertEquals(category.getCreatedAt(), updatedCategory.getCreatedAt());
        assertEquals(category.getDeletedAt(), updatedCategory.getDeletedAt());

        final CategoryJpaEntity foundCategory = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(updatedCategory.getId().getValue(), foundCategory.getId());
        assertEquals(updatedCategory.getName(), foundCategory.getName());
        assertEquals(updatedCategory.getDescription(), foundCategory.getDescription());
        assertEquals(updatedCategory.isActive(), foundCategory.isActive());
        assertEquals(updatedCategory.getCreatedAt(), foundCategory.getCreatedAt());
        assertEquals(updatedCategory.getUpdatedAt(), foundCategory.getUpdatedAt());
        assertEquals(updatedCategory.getDeletedAt(), foundCategory.getDeletedAt());
    }

    @Test
    public void givenPersistedCategories_whenCallFindAll_shouldReturnCategories() {
        assertEquals(0, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "", "name", "asc");

        final Category movies = Category.newCategory("filme", "filme", true);
        final Category series = Category.newCategory("series", "series", true);
        final Category cartoon = Category.newCategory("desenho", "desenho", true);

        categoryRepository.saveAllAndFlush(List.of(
            CategoryJpaEntity.from(movies),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(cartoon)
        ));

        assertEquals(3, categoryRepository.count());

        final Pagination<Category> categoriesPagination = categoryGateway.findAll(query);

        assertEquals(0, categoriesPagination.currentPage());
        assertEquals(3, categoriesPagination.total());
        assertEquals(1, categoriesPagination.perPage());
        assertEquals(cartoon.getId(), categoriesPagination.items().get(0).getId());
    }

    @Test
    public void givenEmptyCategories_whenCallFindAll_shouldReturnEmptyCategories() {
        assertEquals(0, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 3, "", "name", "asc");
        final Pagination<Category> categoriesPagination = categoryGateway.findAll(query);

        assertEquals(0, categoryRepository.count());
        assertEquals(0, categoriesPagination.currentPage());
        assertEquals(0, categoriesPagination.total());
        assertEquals(3, categoriesPagination.perPage());
    }

    @Test
    public void givenFollowPagination_whenCallFindAllWithPage1_shouldReturnCategories() {
        assertEquals(0, categoryRepository.count());

        //page 1
        CategorySearchQuery query = new CategorySearchQuery(0, 1, "", "name", "asc");

        final Category movies = Category.newCategory("filme", "filme", true);
        final Category series = Category.newCategory("series", "series", true);
        final Category cartoon = Category.newCategory("desenho", "desenho", true);

        categoryRepository.saveAllAndFlush(List.of(
            CategoryJpaEntity.from(movies),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(cartoon)
        ));

        assertEquals(3, categoryRepository.count());

        final Pagination<Category> categoriesPage1 = categoryGateway.findAll(query);

        assertEquals(0, categoriesPage1.currentPage());
        assertEquals(3, categoriesPage1.total());
        assertEquals(1, categoriesPage1.perPage());
        assertEquals(categoriesPage1.items().size(), categoriesPage1.perPage());
        assertEquals(cartoon.getId(), categoriesPage1.items().get(0).getId());

        //page 2
        query = new CategorySearchQuery(1, 1, "", "name", "asc");

        final Pagination<Category> categoriesPage2 = categoryGateway.findAll(query);

        assertEquals(1, categoriesPage2.currentPage());
        assertEquals(3, categoriesPage2.total());
        assertEquals(1, categoriesPage2.perPage());
        assertEquals(categoriesPage2.items().size(), categoriesPage2.perPage());
        assertEquals(movies.getId(), categoriesPage2.items().get(0).getId());

        //page 3
        query = new CategorySearchQuery(2, 1, "", "name", "asc");

        final Pagination<Category> categoriesPage3 = categoryGateway.findAll(query);

        assertEquals(2, categoriesPage3.currentPage());
        assertEquals(3, categoriesPage3.total());
        assertEquals(1, categoriesPage3.perPage());
        assertEquals(categoriesPage3.items().size(), categoriesPage3.perPage());
        assertEquals(series.getId(), categoriesPage3.items().get(0).getId());
    }

    @Test
    public void givenPersistedCategoriesAndDesenhoAsTerm_whenCallFindAllAndTermMatchesCategoryName_shouldReturnCategories() {
        assertEquals(0, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "desenho", "name", "asc");

        final Category movies = Category.newCategory("filme", "filme", true);
        final Category series = Category.newCategory("series", "series", true);
        final Category cartoon = Category.newCategory("desenho", "desenho", true);

        categoryRepository.saveAllAndFlush(List.of(
            CategoryJpaEntity.from(movies),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(cartoon)
        ));

        assertEquals(3, categoryRepository.count());

        final Pagination<Category> categoriesPagination = categoryGateway.findAll(query);

        assertEquals(0, categoriesPagination.currentPage());
        assertEquals(1, categoriesPagination.total());
        assertEquals(1, categoriesPagination.perPage());
        assertEquals(cartoon.getId(), categoriesPagination.items().get(0).getId());
    }

    @Test
    public void givenPersistedCategoriesAndMaisAssistidaAsTerm_whenCallFindAllAndTermMatchesCategoryName_shouldReturnCategories() {
        assertEquals(0, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "mais assistida", "name", "asc");

        final Category movies = Category.newCategory("filme", "categoria mais assistida", true);
        final Category series = Category.newCategory("series", "series", true);
        final Category cartoon = Category.newCategory("desenho", "desenho", true);

        categoryRepository.saveAllAndFlush(List.of(
            CategoryJpaEntity.from(movies),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(cartoon)
        ));

        assertEquals(3, categoryRepository.count());

        final Pagination<Category> categoriesPagination = categoryGateway.findAll(query);

        assertEquals(0, categoriesPagination.currentPage());
        assertEquals(1, categoriesPagination.total());
        assertEquals(1, categoriesPagination.perPage());
        assertEquals(categoriesPagination.items().size(), categoriesPagination.perPage());
        assertEquals(movies.getId(), categoriesPagination.items().get(0).getId());
    }
}
