plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":common"))

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-hystrix")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.data:spring-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
