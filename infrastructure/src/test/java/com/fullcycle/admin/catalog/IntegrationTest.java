package com.fullcycle.admin.catalog;

import com.fullcycle.admin.catalog.infrastructure.configuration.WebServerConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Inherited
@ActiveProfiles("integration-test")
@SpringBootTest(classes = WebServerConfiguration.class)
@ExtendWith(CleanUpExtension.class)
public @interface IntegrationTest {}
