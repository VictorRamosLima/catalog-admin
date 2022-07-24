package com.fullcycle.admin.catalog.infrastructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils.like;

@Service
public class CategoryPostgreSQLGateway implements CategoryGateway {
    private final CategoryRepository categoryRepository;

    public CategoryPostgreSQLGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(final Category category) {
        return save(category);
    }

    @Override
    public void deleteById(final CategoryID id) {
        final UUID idValue = id.getValue();
        if (categoryRepository.existsById(idValue)) {
            categoryRepository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return categoryRepository.findById(id.getValue()).map(CategoryJpaEntity::toDomain);
    }

    @Override
    public Category update(final Category category) {
        return save(category);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        Pageable pageRequest = PageRequest.of(
            query.page(),
            query.perPage(),
            Sort.by(Direction.fromString(query.direction()), query.sort())
        );

        final Specification<CategoryJpaEntity> specification = Optional.ofNullable(query.terms())
            .filter(term -> !term.isBlank())
            .map(this::findByNameOrDescription)
            .orElse(null);

        final Page<CategoryJpaEntity> pageResult = categoryRepository.findAll(specification, pageRequest);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(CategoryJpaEntity::toDomain).toList()
        );
    }

    private Category save(final Category category) {
        return categoryRepository.save(CategoryJpaEntity.from(category)).toDomain();
    }

    private Specification<CategoryJpaEntity> findByNameOrDescription(final String term) {
        return SpecificationUtils.<CategoryJpaEntity>like("name", term).or(like("description", term));
    }
}
