plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":common")) {
        this.exclude("org.springframework.boot", "spring-boot-starter-data-redis")
    }
    implementation("com.expediagroup:graphql-kotlin-spring-server:4.1.1")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider:4.1.1")
}

configurations {
    all {
        this.exclude("org.springframework.boot", "spring-boot-starter-data-jpa")
        this.exclude("org.springframework.boot", "spring-boot-starter-data-r2dbc")
        this.exclude("io.r2dbc", "r2dbc-postgresql")
        this.exclude("org.postgresql", "postgresql")
        this.exclude("org.springframework.boot", "spring-boot-starter-data-redis")
    }
}
