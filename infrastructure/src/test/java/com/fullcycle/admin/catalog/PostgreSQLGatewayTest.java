package com.fullcycle.admin.catalog;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.context.annotation.FilterType.REGEX;

@Target(TYPE)
@Retention(RUNTIME)
@Inherited
@PersistenceTest
@ComponentScan(includeFilters = { @Filter(type = REGEX, pattern = ".[PostgreSQLGateway]") })
public @interface PostgreSQLGatewayTest {}
