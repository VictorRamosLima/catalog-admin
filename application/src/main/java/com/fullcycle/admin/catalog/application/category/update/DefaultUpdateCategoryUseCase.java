package com.fullcycle.admin.catalog.application.category.update;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {
    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway, "'categoryGateway' is required");
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(UpdateCategoryCommand command) {
        final Category foundCategory = categoryGateway.findById(command.id())
            .orElseThrow(categoryNotFound(command.id()));

        final Category categoryToUpdate = foundCategory.update(command.name(), command.description(), command.isActive());
        final Notification notification = Notification.create();

        categoryToUpdate.validate(notification);

        return notification.hasErrors() ? Left(notification) : update(categoryToUpdate);
    }

    private Supplier<NotFoundException> categoryNotFound(final CategoryID categoryId) {
        return () -> NotFoundException.from(Category.class, categoryId.toString());
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category category) {
        return Try(() -> categoryGateway.update(category))
            .toEither()
            .bimap(Notification::create, UpdateCategoryOutput::from);
    }
}
