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
    implementation(project(":common"))

    implementation("org.antlr:antlr4-runtime:4.8")
    implementation("com.hazelcast:hazelcast:4.2.1")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.data:spring-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}
