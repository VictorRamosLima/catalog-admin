package com.fullcycle.admin.catalog.e2e.category;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static com.fullcycle.admin.catalog.infrastructure.configuration.json.Json.readValue;
import static com.fullcycle.admin.catalog.infrastructure.configuration.json.Json.writeValueAsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
public class CategoryE2ETest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final PostgreSQLContainer CONTAINER = new PostgreSQLContainer("postgres:14.4")
        .withPassword("root")
        .withUsername("postgres")
        .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final int mappedPort = CONTAINER.getMappedPort(5432);
        System.out.printf("Container is running on port %s", mappedPort);
        registry.add("postgres.port", () -> mappedPort);
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID id = createCategory("Movies", "Most popular movies", true);
        final CategoryResponse category = retrieveCategory(id);

        assertEquals(id.toString(), category.id());
        assertEquals("Movies", category.name());
        assertEquals("Most popular movies", category.description());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertTrue(category.isActive());
        assertNull(category.deletedAt());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void aasACatalogAdminIShouldBeAbleToNavigateThroughAllCategories() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        createCategory("Movies", "Most popular movies", true);
        createCategory("Sports", "Most popular sports in TV", true);
        createCategory("TV Shows", "Most popular TV Shows", true);

        listCategories(0, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Movies")));

        listCategories(1, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Sports")));

        listCategories(2, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("TV Shows")));

        listCategories(3, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchCategoriesByName() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        createCategory("Movies", "Most popular movies", true);
        createCategory("Sports", "Most popular sports in TV", true);
        createCategory("TV Shows", "Most popular TV Shows", true);

        listCategories(0, 1, "movies")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Movies")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortCategoriesByDescriptionDesc() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        createCategory("Movies", "Most popular movies", true);
        createCategory("Sports", "Most popular sports in TV", true);
        createCategory("TV Shows", "Most popular TV Shows", true);

        listCategories(0, 3, "", "description", "desc")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("TV Shows")))
            .andExpect(jsonPath("$.items[1].name", equalTo("Sports")))
            .andExpect(jsonPath("$.items[2].name", equalTo("Movies")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToRetrieveACategoryByItsIdentifier() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID id = createCategory("Movies", "Most popular movies", true);
        final CategoryResponse category = retrieveCategory(id);

        assertEquals(id.toString(), category.id());
        assertEquals("Movies", category.name());
        assertEquals("Most popular movies", category.description());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertTrue(category.isActive());
        assertNull(category.deletedAt());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID id = createCategory("Movie", null, true);

        final UpdateCategoryRequest requestBody = new UpdateCategoryRequest("Movies", "Most popular movies", true);
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{id}", id.toString())
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .content(writeValueAsString(requestBody));

        mvc.perform(request).andExpect(status().isOk());

        final CategoryResponse category = retrieveCategory(id);

        assertEquals(id.toString(), category.id());
        assertEquals("Movies", category.name());
        assertEquals("Most popular movies", category.description());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertTrue(category.isActive());
        assertNull(category.deletedAt());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToActivateACategoryByItsIdentifier() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID id = createCategory("Movies", "Most popular movies", false);

        final UpdateCategoryRequest requestBody = new UpdateCategoryRequest("Movies", "Most popular movies", true);
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{id}", id.toString())
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .content(writeValueAsString(requestBody));

        mvc.perform(request).andExpect(status().isOk());

        final CategoryResponse category = retrieveCategory(id);

        assertEquals(id.toString(), category.id());
        assertEquals("Movies", category.name());
        assertEquals("Most popular movies", category.description());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertTrue(category.isActive());
        assertNull(category.deletedAt());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToInactivateACategoryByItsIdentifier() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID id = createCategory("Movies", "Most popular movies", true);

        final UpdateCategoryRequest requestBody = new UpdateCategoryRequest("Movies", "Most popular movies", false);
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{id}", id.toString())
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .content(writeValueAsString(requestBody));

        mvc.perform(request).andExpect(status().isOk());

        final CategoryResponse category = retrieveCategory(id);

        assertEquals(id.toString(), category.id());
        assertEquals("Movies", category.name());
        assertEquals("Most popular movies", category.description());
        assertNotNull(category.createdAt());
        assertNotNull(category.updatedAt());
        assertNotNull(category.deletedAt());
        assertFalse(category.isActive());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeHandledErrorByGettingANotPersistedCategory() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories/123")
            .accept(APPLICATION_JSON_VALUE);

        mvc.perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", equalTo("Category with id 123 not found")))
            .andExpect(jsonPath("$.errors").isEmpty());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID categoryId = createCategory("Movies", "Most popular movies", true);
        assertEquals(1, categoryRepository.count());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .delete("/categories/{categoryId}", categoryId)
            .accept(APPLICATION_JSON_VALUE);

        mvc.perform(request).andExpect(status().isNoContent());

        assertEquals(0, categoryRepository.count());
        assertFalse(categoryRepository.existsById(categoryId.getValue()));
    }

    @Test
    public void asACatalogAdminIShouldGetA204ResponseWhenTheCategoryToBeDeletedIsNotFound() throws Exception {
        assertTrue(CONTAINER.isRunning());
        assertEquals(0, categoryRepository.count());

        final CategoryID categoryId = CategoryID.unique();

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .delete("/categories/{categoryId}", categoryId.toString())
            .accept(APPLICATION_JSON_VALUE);

        mvc.perform(request).andExpect(status().isNoContent());

        assertEquals(0, categoryRepository.count());
        assertFalse(categoryRepository.existsById(categoryId.getValue()));
    }

    private CategoryID createCategory(final String name, final String description, final boolean isActive) throws Exception {
        final CreateCategoryRequest requestBody = new CreateCategoryRequest(name, description, isActive);
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post("/categories")
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .content(writeValueAsString(requestBody));

        final String id = mvc.perform(request)
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader(LOCATION)
            .replace("/categories/", "");

        return CategoryID.from(id);
    }

    private CategoryResponse retrieveCategory(final CategoryID id) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories/{categoryId}", id.toString())
            .accept(APPLICATION_JSON_VALUE);

        final String rawResponse = mvc.perform(request)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return readValue(rawResponse, CategoryResponse.class);
    }

    private ResultActions listCategories(final int page, final int size, final String search) throws Exception {
        return listCategories(page, size, "movies", "", "");
    }

    private ResultActions listCategories(final int page, final int size) throws Exception {
        return listCategories(page, size, "", "", "");
    }

    private ResultActions listCategories(
        final int page,
        final int size,
        final String search,
        final String sort,
        final String order
    ) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .queryParam("search", search)
            .queryParam("sort", sort)
            .queryParam("order", order)
            .accept(APPLICATION_JSON_VALUE);

        return mvc.perform(request);
    }
}
