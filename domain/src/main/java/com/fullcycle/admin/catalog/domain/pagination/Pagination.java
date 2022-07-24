package com.fullcycle.admin.catalog.domain.pagination;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public record Pagination<T>(int currentPage, int perPage, long total, List<T> items) {
    public <R> Pagination<R> map(final Function<T, R> mapper) {
        final List<R> mappedCollection = items().stream().map(mapper).collect(toList());
        return new Pagination<>(currentPage(), perPage(), total(), mappedCollection);
    }
}
