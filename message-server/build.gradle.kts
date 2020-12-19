plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

group = "com.qingzhu"
version = "0.0.1-SNAPSHOT"


dependencies {
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")

    implementation("com.hazelcast:hazelcast-all:4.0.2")
    /** k8s 依赖 **/
    implementation("com.hazelcast:hazelcast-kubernetes:2.2")

    runtimeOnly("org.postgresql:postgresql")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    // testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
    testImplementation("org.springframework.cloud:spring-cloud-stream")
}
