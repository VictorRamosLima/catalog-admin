import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "9.0.0"
}

group = "com.fullcycle.admin.catalog.infrastructure"
version = "1.0-SNAPSHOT"

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("application.jar")
    destinationDirectory.set(file("${rootProject.buildDir}/libs"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation("io.vavr:vavr:0.10.4")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")
    implementation("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.6.9")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    testImplementation("org.testcontainers:testcontainers:1.17.3")
    testImplementation("org.testcontainers:postgresql:1.17.3")
    testImplementation("org.testcontainers:junit-jupiter:1.17.3")
}

flyway {
    url = System.getenv("FLYWAY_DB") ?: "jdbc:postgresql://localhost:5432/adm_videos"
    user = System.getenv("FLYWAY_USER") ?: "postgres"
    password = System.getenv("FLYWAY_PASS") ?: "root"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
