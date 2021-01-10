plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 安全配置
    implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
    implementation("org.springframework.cloud:spring-cloud-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // implementation("org.springframework.data:spring-data-elasticsearch")
    // implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
}
