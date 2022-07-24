package com.fullcycle.admin.catalog;

import com.fullcycle.admin.catalog.infrastructure.configuration.ObjectMapperConfiguration;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@Target(TYPE)
@Retention(RUNTIME)
@Inherited
@ActiveProfiles("integration-test")
@JsonTest(includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = ObjectMapperConfiguration.class))
public @interface JacksonTest {}
