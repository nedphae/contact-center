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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // 不使用 Spring Security 保户网关
    // implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    // implementation("org.springframework.boot:spring-boot-starter-security")
    // implementation("org.springframework.security:spring-security-oauth2-resource-server")
    // implementation("org.springframework.security:spring-security-oauth2-jose")

    // spring security 与 gateway 还未进行整合，需要自己实现 filter
    // implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
    // implementation("org.springframework.cloud:spring-cloud-starter-security")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
