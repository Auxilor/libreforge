pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "libreforge"

include(":core")
include(":core:common")
include(":loader")
include(":gradle-plugin")
