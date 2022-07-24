package com.fullcycle.admin.catalog.infrastructure.api;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.junit.jupiter.api.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag("Categories")
@RequestMapping(value = "categories")
public interface CategoryAPI {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created"),
        @ApiResponse(responseCode = "422", description = "Unprocessable entity error"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    ResponseEntity<?> create(@RequestBody final CreateCategoryRequest input);

    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "List all categories paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns a list of categories"),
        @ApiResponse(responseCode = "400", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    Pagination<?> index(
        @RequestParam(value = "search", required = false, defaultValue = "")
        final String search,
        @RequestParam(value = "page", required = false, defaultValue = "0")
        final int page,
        @RequestParam(value = "size", required = false, defaultValue = "10")
        final int perPage,
        @RequestParam(value = "sort", required = false, defaultValue = "name")
        final String sort,
        @RequestParam(value = "order", required = false, defaultValue = "asc")
        final String order
    );

    @ResponseStatus(OK)
    @GetMapping(value = "{categoryId}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the category"),
        @ApiResponse(responseCode = "400", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "400", description = "Category was not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    CategoryResponse show(@PathVariable final String categoryId);

    @ResponseStatus(OK)
    @PutMapping(value = "{categoryId}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    ResponseEntity<?> update(
        @PathVariable final String categoryId,
        @RequestBody final UpdateCategoryRequest input
    );

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(value = "{categoryId}")
    @Operation(summary = "Deletes a category by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "400", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    void delete(@PathVariable final String categoryId);
}
