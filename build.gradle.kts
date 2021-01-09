import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.qingzhu"
version = "1.0-SNAPSHOT"

plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.72" apply false
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72" apply false
    kotlin("plugin.jpa") version "1.3.72" apply false
    kotlin("kapt") version "1.3.72"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")

    java.sourceCompatibility = JavaVersion.VERSION_11

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            url = uri("https://repo.spring.io/libs-milestone")
        }
        maven {
            url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/")
        }
    }

    extra["springCloudVersion"] = "Hoxton.SR9"

    val arrowVersion = "0.10.5"

    dependencies {
        // Kotlin 依赖
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

        // Typed Functional Programming in Kotlin
        implementation("io.arrow-kt:arrow-core:$arrowVersion")
        implementation("io.arrow-kt:arrow-fx:$arrowVersion")
        implementation("io.arrow-kt:arrow-optics:$arrowVersion")
        implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
        kapt("io.arrow-kt:arrow-meta:$arrowVersion")

        // rxjava
        implementation("io.reactivex.rxjava2:rxjava:2.2.14")
        implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

        // jackson 依赖
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        // spring 依赖
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
        implementation("org.springframework.boot:spring-boot-devtools")

        // 安全配置
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.security:spring-security-oauth2-resource-server")
        implementation("org.springframework.security:spring-security-oauth2-client")
        implementation("org.springframework.security:spring-security-oauth2-jose")
        implementation("org.springframework.cloud:spring-cloud-starter-oauth2")
        implementation("org.springframework.cloud:spring-cloud-starter-security")

        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

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
}
