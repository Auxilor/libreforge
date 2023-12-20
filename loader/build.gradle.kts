dependencies {
    compileOnly(project(":core"))

    compileOnly("com.willfp:eco:6.67.3")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}

group = "com.willfp"
version = rootProject.version

tasks {
    build {
        dependsOn(publishToMavenLocal)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "libreforge-loader"
            groupId = "com.willfp"
        }
    }
}
