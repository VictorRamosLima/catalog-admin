package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import com.fullcycle.admin.catalog.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.api.CategoryAPI;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.presenter.CategoryApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CategoryController implements CategoryAPI {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(
        final CreateCategoryUseCase createCategoryUseCase,
        final GetCategoryByIdUseCase getCategoryByIdUseCase,
        final UpdateCategoryUseCase updateCategoryUseCase,
        final DeleteCategoryUseCase deleteCategoryUseCase,
        final ListCategoriesUseCase listCategoriesUseCase
    ) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoriesUseCase = Objects.requireNonNull(listCategoriesUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCategoryRequest input) {
        final CreateCategoryCommand command = CreateCategoryCommand.from(
            input.name(),
            input.description(),
            input.isActive() == null || input.isActive()
        );

        return createCategoryUseCase.execute(command).fold(
            ResponseEntity.unprocessableEntity()::body,
            output -> ResponseEntity.created(URI.create("/categories/" + output.id())).body(output)
        );
    }

    @Override
    public Pagination<?> index(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String order
    ) {
        return listCategoriesUseCase.execute(new CategorySearchQuery(page, perPage, search, sort, order))
            .map(CategoryApiPresenter::present);
    }

    @Override
    public CategoryResponse show(final String categoryId) {
        return CategoryApiPresenter.present(getCategoryByIdUseCase.execute(categoryId));
    }

    @Override
    public ResponseEntity<?> update(final String categoryId, final UpdateCategoryRequest input) {
        final UpdateCategoryCommand command = UpdateCategoryCommand.from(
            CategoryID.from(categoryId),
            input.name(),
            input.description(),
            input.isActive() == null || input.isActive()
        );

        return updateCategoryUseCase.execute(command).fold(
            ResponseEntity.unprocessableEntity()::body,
            ResponseEntity::ok
        );
    }

    @Override
    public void delete(final String categoryId) {
        deleteCategoryUseCase.execute(categoryId);
    }
}
