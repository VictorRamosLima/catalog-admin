package com.fullcycle.admin.catalog.infrastructure.configuration.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.Callable;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public enum Json {
    INSTANCE;

    public static ObjectMapper getMapper() {
        return INSTANCE.objectMapper.copy();
    }

    public static <T> T readValue(final String json, final Class<T> type) {
        return invoke(() -> INSTANCE.objectMapper.readValue(json, type));
    }

    public static String writeValueAsString(final Object value) {
        return invoke(() -> INSTANCE.objectMapper.writeValueAsString(value));
    }

    private final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
        .dateFormat(new StdDateFormat())
        .featuresToDisable(
            FAIL_ON_UNKNOWN_PROPERTIES,
            FAIL_ON_NULL_FOR_PRIMITIVES,
            FAIL_ON_NULL_CREATOR_PROPERTIES,
            WRITE_DATES_AS_TIMESTAMPS
        )
        .modules(new JavaTimeModule(), new Jdk8Module(), afterburnerModule())
        .propertyNamingStrategy(SNAKE_CASE)
        .build();

    public <T> T fromJson(String json, Class<T> clazz) {
        return null;
    }

    private AfterburnerModule afterburnerModule() {
        final var module = new AfterburnerModule();
        module.setUseValueClassLoader(false);

        return module;
    }

    private static <T> T invoke(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
