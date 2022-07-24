package com.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.handler.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import io.vavr.API;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        final CategoryID categoryId = CategoryID.unique();
        final CreateCategoryRequest input = new CreateCategoryRequest("filme", "descrição", true);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(API.Right(CreateCategoryOutput.from(categoryId)));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(LOCATION, "/categories/" + categoryId))
            .andExpect(jsonPath("$.id").value(categoryId.toString()))
            .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        verify(createCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenACommandWithNullName_whenCallsCreateCategory_shouldReturnError() throws Exception {
        final CreateCategoryRequest input = new CreateCategoryRequest(null, "descrição", true);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(Left(Notification.create(Error.of("'name' cannot be null"))));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(LOCATION, nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo("'name' cannot be null")));

        verify(createCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCategoryAndUseCaseReturnsError_shouldReturnParsedError() throws Exception {
        final CreateCategoryRequest input = new CreateCategoryRequest("Filme", "descrição", true);

        when(createCategoryUseCase.execute(any()))
            .thenThrow(DomainException.from(Error.of("'name' cannot be null")));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(LOCATION, nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo("'name' cannot be null")))
            .andExpect(jsonPath("$.message", equalTo("One or more errors occurred during validation")));

        verify(createCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenValidId_whenCallGetCategory_shouldReturnCategory() throws Exception {
        final Category category = Category.newCategory("filme", "descrição", true);
        final CategoryID categoryId = category.getId();

        when(getCategoryByIdUseCase.execute(any())).thenReturn(CategoryOutput.from(category));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories/{categoryId}", categoryId)
            .accept(APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(categoryId.toString()))
            .andExpect(jsonPath("$.name").value("filme"))
            .andExpect(jsonPath("$.description").value("descrição"))
            .andExpect(jsonPath("$.is_active").value(true))
            .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        verify(getCategoryByIdUseCase, times(1)).execute(eq(categoryId.toString()));
    }

    @Test
    public void givenValidId_whenCallGetCategoryThatDoesNotExist_shouldReturnNotFound() throws Exception {
        final CategoryID categoryId = CategoryID.unique();
        final String expectedErrorMessage = "Category with id " + categoryId + " not found";

        when(getCategoryByIdUseCase.execute(any()))
            .thenThrow(NotFoundException.from(Category.class, categoryId.toString()));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories/{categoryId}", categoryId)
            .accept(APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errors").isEmpty())
            .andExpect(jsonPath("$.message").value(expectedErrorMessage));

        verify(getCategoryByIdUseCase, times(1)).execute(eq(categoryId.toString()));
    }

    @Test
    public void givenValidIdAndValidBody_whenCallUpdateCategory_shouldUpdateAndReturnCategory() throws Exception {
        final Category category = Category.newCategory("filme", "descrição", true);
        final CategoryID categoryId = category.getId();

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(Right(UpdateCategoryOutput.from(category)));

        final UpdateCategoryRequest input = new UpdateCategoryRequest("filme", "descrição", true);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{categoryId}", categoryId)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(categoryId.toString()))
            .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.id(), categoryId);
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenValidIdAndInvalidName_whenCallUpdateCategory_shouldNotUpdateAndReturnError() throws Exception {
        final Category category = Category.newCategory(null, "descrição", true);
        final CategoryID categoryId = category.getId();
        final String expectedErrorMessage = "'name' cannot be null";

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(Left(Notification.create(Error.of(expectedErrorMessage))));

        final UpdateCategoryRequest input = new UpdateCategoryRequest("filme", "descrição", true);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{categoryId}", categoryId)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors").isNotEmpty())
            .andExpect(jsonPath("$.errors[0].message").value(expectedErrorMessage));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.id(), categoryId);
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenValidIdAndValidBody_whenCategoryIsNotFound_shouldNotUpdateAndReturnError() throws Exception {
        final CategoryID categoryId = CategoryID.unique();
        final String expectedErrorMessage = "Category with id " + categoryId + " not found";

        when(updateCategoryUseCase.execute(any()))
            .thenThrow(NotFoundException.from(Category.class, categoryId.toString()));

        final UpdateCategoryRequest input = new UpdateCategoryRequest("filme", "descrição", true);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put("/categories/{categoryId}", categoryId)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(input));

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(expectedErrorMessage));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.id(), categoryId);
            assertEquals(command.name(), input.name());
            assertEquals(command.description(), input.description());
            assertEquals(command.isActive(), input.isActive());
            return true;
        }));
    }

    @Test
    public void givenValidId_whenCallDeleteCategory_shouldDeleteCategory() throws Exception {
        final CategoryID categoryId = CategoryID.unique();

        doNothing().when(deleteCategoryUseCase).execute(any());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/categories/{categoryId}", categoryId);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(eq(categoryId.toString()));
    }

    @Test
    public void givenValidParams_whenCallListCategories_shouldReturnCategories() throws Exception {
        final Category category = Category.newCategory("movies", "description", true);
        final CategoryListOutput expectedOutput = CategoryListOutput.from(category);
        final List<CategoryListOutput> items = List.of(expectedOutput);

        when(listCategoriesUseCase.execute(any()))
            .thenReturn(new Pagination<>(0, 10, 1, items));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/categories")
            .queryParam("page", "0")
            .queryParam("size", "10")
            .queryParam("search", "movies")
            .queryParam("sort", "description")
            .queryParam("order", "desc")
            .accept(APPLICATION_JSON);

        mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page").value(0))
            .andExpect(jsonPath("$.per_page").value(10))
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id").value(category.getId().toString()))
            .andExpect(jsonPath("$.items[0].name").value("movies"))
            .andExpect(jsonPath("$.items[0].description").value("description"))
            .andExpect(jsonPath("$.items[0].is_active").value(true))
            .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        verify(listCategoriesUseCase, times(1)).execute(argThat(command -> {
            assertEquals(command.page(), 0);
            assertEquals(command.perPage(), 10);
            assertEquals(command.terms(), "movies");
            assertEquals(command.sort(), "description");
            assertEquals(command.direction(), "desc");
            return true;
        }));
    }
}
