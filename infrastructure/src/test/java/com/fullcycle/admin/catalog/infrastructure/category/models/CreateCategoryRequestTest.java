package com.fullcycle.admin.catalog.infrastructure.category.models;

import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

@JacksonTest
public class CreateCategoryRequestTest {
    @Autowired
    private JacksonTester<CreateCategoryRequest> mapper;

    @Test
    public void testMarshall() throws Exception {
        final String expectedName = "Category";
        final String expectedDescription = "Description";
        final boolean expectedIsActive = true;

        final CreateCategoryRequest request = new CreateCategoryRequest(
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        final JsonContent<CreateCategoryRequest> json = mapper.write(request);

        Assertions.assertThat(json)
            .hasJsonPathValue("$.name", expectedName)
            .hasJsonPathValue("$.description", expectedDescription)
            .hasJsonPathValue("$.is_active", expectedIsActive);
    }

    @Test
    public void testUnmarshall() throws Exception {
        final String expectedName = "Category";
        final String expectedDescription = "Description";
        final boolean expectedIsActive = true;

        final String json = """
            {
                "name": "%s",
                "description": "%s",
                "is_active": %s
            }
        """.formatted(expectedName, expectedDescription, expectedIsActive);

        final ObjectContent<CreateCategoryRequest> response = mapper.parse(json);

        Assertions.assertThat(response)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("description", expectedDescription)
            .hasFieldOrPropertyWithValue("isActive", expectedIsActive);
    }
}
