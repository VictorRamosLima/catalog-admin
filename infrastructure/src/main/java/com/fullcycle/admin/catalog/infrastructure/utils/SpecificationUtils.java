package com.fullcycle.admin.catalog.infrastructure.utils;

import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtils {
    private SpecificationUtils() {}

    public static <T> Specification<T> like(final String property, final String term) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.upper(root.get(property)),
            like(term.toUpperCase())
        );
    }

    public static String like(final String term) {
        return "%" + term + "%";
    }
}
