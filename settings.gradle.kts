pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

rootProject.name = "contact-center-all"

include("bot", "gateway", "common", "im-access",
        "dispatching-center", "staff-admin", "message-server")
