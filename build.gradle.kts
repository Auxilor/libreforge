buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    }
}

plugins {
    java
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}

dependencies {
    implementation(project(":core"))
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://nexuslite.gcnt.net/repos/other/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")

        compileOnly(fileTree("lib") { include("*.jar") })
    }

    publishing {
        repositories {
            maven {
                name = "auxilor"
                url = uri("https://repo.auxilor.io/repository/maven-releases/")
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.processResources {
        filesMatching(listOf("**plugin.yml")) {
            expand("projectVersion" to rootProject.version)
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            dependsOn(clean)
        }

        build {
            dependsOn(shadowJar)
            dependsOn(publishToMavenLocal)
        }
    }
}

group = "com.willfp"
version = findProperty("version")!!

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.build {
    dependsOn("shadowJar")
    dependsOn("publishToMavenLocal")
}
