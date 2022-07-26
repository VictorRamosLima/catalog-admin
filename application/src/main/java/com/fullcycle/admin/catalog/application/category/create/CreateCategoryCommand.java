package com.fullcycle.admin.catalog.application.category.create;

public record CreateCategoryCommand(String name, String description, boolean isActive) {
    public static CreateCategoryCommand from(final String name, final String description, final boolean isActive) {
        return new CreateCategoryCommand(name, description, isActive);
    }
}
