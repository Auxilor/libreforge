group = "com.willfp"
version = rootProject.version

subprojects {
    dependencies {
        compileOnly("com.willfp:eco:6.77.0")
    }
}

dependencies {
    implementation(project(":core:common"))
}

tasks {
    shadowJar {
        relocate("dev.romainguy.kotlin.math", "com.willfp.libreforge.libs.math")
        relocate("org.apache.maven", "com.willfp.eco.libs.maven")
        relocate("com.willfp.modelenginebridge", "com.willfp.libreforge.libs.modelenginebridge")
    }

    build {
        dependsOn("publishToMavenLocal")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifactId = "libreforge"
            groupId = "com.willfp"

            artifact(tasks.shadowJar.get().archiveFile.get()) {
                classifier = "shadow"
            }
        }
    }
}
