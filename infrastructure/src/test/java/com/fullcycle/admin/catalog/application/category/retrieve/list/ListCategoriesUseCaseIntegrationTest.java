package com.fullcycle.admin.catalog.application.category.retrieve.list;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListCategoriesUseCaseIntegrationTest {
    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void mockUp() {
        final List<CategoryJpaEntity> categories = Stream.of(
            Category.newCategory("Filmes", "Os melhores filmes", true),
            Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
            Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
            Category.newCategory("Documentários", "Documentários de vários tipos", true),
            Category.newCategory("Animes", "Títulos de animes dublados e legendados", true),
            Category.newCategory("Sports", "Jogos de sport", true),
            Category.newCategory("Kids", "Categoria para crianças", true),
            Category.newCategory("Series", "Series de vários tipos", true)
        ).map(CategoryJpaEntity::from).toList();

        repository.saveAllAndFlush(categories);
    }

    @Test
    public void givenAValidTerm_whenTermDoesNotMatchPersisted_shouldReturnEmpty() {
        final CategorySearchQuery query = new CategorySearchQuery(0, 10, "random", "name", "asc");

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertEquals(0, result.total());
        assertEquals(0, result.items().size());
        assertEquals(0, result.currentPage());
        assertEquals(10, result.perPage());
    }

    @ParameterizedTest
    @CsvSource({
        "fil,0,10,1,1,Filmes",
        "net,0,10,1,1,Netflix Originals",
        "ZON,0,10,1,1,Amazon Originals",
        "KI,0,10,1,1,Kids",
        "crianças,0,10,1,1,Kids",
        "da Amazon,0,10,1,1,Amazon Originals",
    })
    public void givenAValidTerm_whenTermMatchesPersisted_shouldReturnList(
        final String term,
        final int page,
        final int perPage,
        final int itemsCount,
        final int expectedTotal,
        final String expectedName
    ) {
        final CategorySearchQuery query = new CategorySearchQuery(page, perPage, term, "name", "asc");

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertEquals(expectedTotal, result.total());
        assertEquals(itemsCount, result.items().size());
        assertEquals(page, result.currentPage());
        assertEquals(perPage, result.perPage());
        assertEquals(expectedName, result.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "name,asc,0,10,8,8,Amazon Originals",
        "name,desc,0,10,8,8,Sports",
        "description,asc,0,10,8,8,Kids",
        "description,desc,0,10,8,8,Netflix Originals",
    })
    public void givenAValidSortAndDirection_whenCallListCategories_shouldReturnList(
        final String expectedSort,
        final String expectedDirection,
        final int page,
        final int perPage,
        final int itemsCount,
        final int expectedTotal,
        final String expectedName
    ) {
        final CategorySearchQuery query = new CategorySearchQuery(page, perPage, "", expectedSort, expectedDirection);

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertEquals(expectedTotal, result.total());
        assertEquals(itemsCount, result.items().size());
        assertEquals(page, result.currentPage());
        assertEquals(perPage, result.perPage());
        assertEquals(expectedName, result.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,8,Amazon Originals;Animes",
        "1,2,2,8,Documentários;Filmes",
        "2,2,2,8,Kids;Netflix Originals",
        "3,2,2,8,Series;Sports",
    })
    public void givenAValidPage_whenCallListCategories_shouldReturnListPaginated(
        final int page,
        final int perPage,
        final int itemsCount,
        final int expectedTotal,
        final String expectedNames
    ) {
        final CategorySearchQuery query = new CategorySearchQuery(page, perPage, "", "name", "asc");

        final Pagination<CategoryListOutput> result = useCase.execute(query);

        assertEquals(expectedTotal, result.total());
        assertEquals(itemsCount, result.items().size());
        assertEquals(page, result.currentPage());
        assertEquals(perPage, result.perPage());

        int index = 0;
        for (final String expectedName : expectedNames.split(";")) {
            assertEquals(expectedName, result.items().get(index).name());
            index++;
        }
    }
}
