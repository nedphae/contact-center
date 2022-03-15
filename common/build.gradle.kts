plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jetbrains.kotlin.plugin.noarg")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks {
    // 不打包成 spring boot jar
    bootJar {
        enabled = false
    }
    // 启用普通 jar 打包方式
    jar {
        enabled = true
    }
}
