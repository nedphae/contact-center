plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
    id("org.jetbrains.dokka")
}

group = "com.qingzhu"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))

    implementation("org.antlr:antlr4-runtime:4.8")
    implementation("com.lmax:disruptor:3.3.6")

    implementation("com.corundumstudio.socketio:netty-socketio:1.7.18")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("io.minio:minio:8.2.0")
    implementation("commons-io:commons-io:2.8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
