package com.fullcycle.admin.catalog.infrastructure.category.models;

import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.time.Instant;
import java.util.UUID;

@JacksonTest
public class CategoryResponseTest {
    @Autowired
    private JacksonTester<CategoryResponse> mapper;

    @Test
    public void testMarshall() throws Exception {
        final String expectedCategoryId = UUID.randomUUID().toString();
        final String expectedName = "Category";
        final String expectedDescription = "Description";
        final boolean expectedIsActive = false;
        final Instant expectedCreatedAt = Instant.now();
        final Instant expectedUpdatedAt = Instant.now();
        final Instant expectedDeletedAt = Instant.now();

        final CategoryResponse response = new CategoryResponse(
            expectedCategoryId,
            expectedName,
            expectedDescription,
            expectedIsActive,
            expectedCreatedAt,
            expectedUpdatedAt,
            expectedDeletedAt
        );

        final JsonContent<CategoryResponse> json = mapper.write(response);

        Assertions.assertThat(json)
            .hasJsonPathValue("$.id", expectedCategoryId)
            .hasJsonPathValue("$.name", expectedName)
            .hasJsonPathValue("$.description", expectedDescription)
            .hasJsonPathValue("$.is_active", expectedIsActive)
            .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
            .hasJsonPathValue("$.updated_at", expectedUpdatedAt.toString())
            .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    public void testUnmarshall() throws Exception {
        final String expectedCategoryId = UUID.randomUUID().toString();
        final String expectedName = "Category";
        final String expectedDescription = "Description";
        final boolean expectedIsActive = false;
        final Instant expectedCreatedAt = Instant.now();
        final Instant expectedUpdatedAt = Instant.now();
        final Instant expectedDeletedAt = Instant.now();

        final String json = """
            {
                "id": "%s",
                "name": "%s",
                "description": "%s",
                "is_active": %s,
                "created_at": "%s",
                "updated_at": "%s",
                "deleted_at": "%s"
            }
        """.formatted(
            expectedCategoryId,
            expectedName,
            expectedDescription,
            expectedIsActive,
            expectedCreatedAt,
            expectedUpdatedAt,
            expectedDeletedAt
        );

        final ObjectContent<CategoryResponse> response = mapper.parse(json);

        Assertions.assertThat(response)
            .hasFieldOrPropertyWithValue("id", expectedCategoryId)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("description", expectedDescription)
            .hasFieldOrPropertyWithValue("isActive", expectedIsActive)
            .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
            .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt)
            .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt);
    }
}
