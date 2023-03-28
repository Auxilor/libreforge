plugins {
    id("java-gradle-plugin")
    id("maven-publish")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
}

group = "com.willfp"
version = "1.0.0"

tasks {
    build {
        dependsOn(publishToMavenLocal)
    }
}

gradlePlugin {
    plugins {
        create("libreforge-gradle-plugin") {
            id = "libreforge-gradle-plugin"
            implementationClass = "com.willfp.libreforge.gradle.LibreforgeGradlePlugin"
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "libreforge-gradle-plugin"
        }
    }
}
