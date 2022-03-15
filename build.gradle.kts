import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.21"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32" apply false
    kotlin("plugin.jpa") version "1.4.32" apply false
    kotlin("kapt") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    id("net.ltgt.apt") version "0.15"
}

group = "com.qingzhu"
version = "1.0.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/spring") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "net.ltgt.apt-idea")

    noArg {
        annotation("com.qingzhu.common.constant.NoArg")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    extra["springCloudVersion"] = "2020.0.2"
    extra["springDataBom"] = "2020.0.3"

    val arrowVersion = "0.11.0"

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
            mavenBom("org.springframework.data:spring-data-bom:${property("springDataBom")}")
            mavenBom("io.arrow-kt:arrow-stack:$arrowVersion")
            // mavenBom("io.r2dbc:r2dbc-bom:Arabba-SR8")
        }
    }

    dependencies {
        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

        // Functional Programming in Kotlin
        implementation("io.arrow-kt:arrow-core")
        implementation("io.arrow-kt:arrow-fx")
        implementation("io.arrow-kt:arrow-fx-coroutines")
        implementation("io.arrow-kt:arrow-optics")
        implementation("io.arrow-kt:arrow-syntax")
        implementation("io.arrow-kt:arrow-aql")
        kapt("io.arrow-kt:arrow-meta")

        // jackson
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        implementation("org.zalando:problem-spring-webflux:0.26.2")

        // spring
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-webflux")

        // spring cloud
        implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
        implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-devtools")

        // jpa postgresql
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
        runtimeOnly("io.r2dbc:r2dbc-postgresql")
        runtimeOnly("org.postgresql:postgresql")

        // security
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.security:spring-security-oauth2-resource-server")
        implementation("org.springframework.security:spring-security-oauth2-client")
        implementation("org.springframework.security:spring-security-oauth2-jose")

        // mapstruct
        implementation("org.mapstruct:mapstruct:1.4.1.Final")
        kapt("org.mapstruct:mapstruct-processor:1.4.1.Final")
        kaptTest("org.mapstruct:mapstruct-processor:1.4.1.Final")

        kapt("org.springframework.boot:spring-boot-configuration-processor")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
        testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
        // testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
        testImplementation("org.springframework.cloud:spring-cloud-stream")
        testImplementation("com.squareup.okhttp3:mockwebserver")
        // testRuntimeOnly("com.squareup.okhttp3:mockwebserver3-junit5:4.9.0")
    }
}
