pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/spring-plugin") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

rootProject.name = "contact-center-all"

include(
    "bot", "gateway", "common", "im-access",
    "dispatching-center", "staff-admin", "message-server", "GraphQLBFF"
)
