package com.fullcycle.admin.catalog.infrastructure.category.models;

import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;

@JacksonTest
public class UpdateCategoryRequestTest {
    @Autowired
    private JacksonTester<UpdateCategoryRequest> mapper;

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

        final ObjectContent<UpdateCategoryRequest> response = mapper.parse(json);

        Assertions.assertThat(response)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("description", expectedDescription)
            .hasFieldOrPropertyWithValue("isActive", expectedIsActive);
    }
}
