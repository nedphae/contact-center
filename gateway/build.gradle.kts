plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
}

configurations {
    all {
        this.exclude("org.springframework.boot", "spring-boot-starter-data-jpa")
        this.exclude("org.springframework.boot", "spring-boot-starter-data-r2dbc")
        this.exclude("io.r2dbc", "r2dbc-postgresql")
        this.exclude("org.postgresql", "postgresql")
    }
}