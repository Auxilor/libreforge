package com.willfp.libreforge.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class LibreforgeGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configurations.create("libreforge")

        val libreforgeJar = project.tasks.register("libreforgeJar", Jar::class.java) {
            it.destinationDirectory.set(project.file("${project.rootDir}/bin"))
            it.archiveFileName.set("${project.name} v${project.version}.jar")

            it.dependsOn(project.tasks.getByName("shadowJar"))

            it.from(
                project.configurations.getByName("libreforge")
                        + project.tasks.getByName("shadowJar").outputs.files.map { file -> project.zipTree(file) }

            )
        }

        project.tasks.named("build").configure {
            it.dependsOn(libreforgeJar)
        }

        val configuration = project.configurations.maybeCreate("libreforge")
        configuration.isTransitive = false

        project.afterEvaluate {
            val libreforgeVersion: String = project.findProperty("libreforge-version") as? String
                ?: throw IllegalStateException("libreforge-version must be specified in gradle.properties!")

            val libreforge = project.configurations.getByName("libreforge")

            libreforge.dependencies.add(project.dependencies.create("com.willfp:libreforge:$libreforgeVersion:shadow"))

            val implementation = project.configurations.getByName("implementation")
            implementation.dependencies.add(project.dependencies.create("com.willfp:libreforge-loader:$libreforgeVersion"))

            for (subproject in it.subprojects) {
                val compileOnly = subproject.configurations.getByName("compileOnly")
                compileOnly.dependencies.add(subproject.dependencies.create("com.willfp:libreforge:$libreforgeVersion:shadow"))
                compileOnly.dependencies.add(subproject.dependencies.create("com.willfp:libreforge-loader:$libreforgeVersion"))
            }
        }

        project.tasks.register("cleanLibreforgeJar") {
            it.doLast {
                project.file("${project.rootDir}/bin").deleteRecursively()
            }
        }

        project.tasks.named("clean").configure {
            it.dependsOn(project.tasks.getByName("cleanLibreforgeJar"))
        }
    }
}
