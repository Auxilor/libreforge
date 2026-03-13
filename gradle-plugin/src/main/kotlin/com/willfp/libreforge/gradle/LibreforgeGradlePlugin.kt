package com.willfp.libreforge.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

class LibreforgeGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configurations.create("libreforge")

        val configuration = project.configurations.maybeCreate("libreforge")
        configuration.isTransitive = false

        project.afterEvaluate {
            val libreforgeVersion: String = project.findProperty("libreforge-version") as? String
                ?: throw IllegalStateException("libreforge-version must be specified in gradle.properties!")

            val libreforge = project.configurations.getByName("libreforge")
            val compile = project.configurations.getByName("compileOnly")

            libreforge.dependencies.add(project.dependencies.create("com.willfp:libreforge:$libreforgeVersion:shadow"))
            compile.dependencies.add(project.dependencies.create("com.willfp:libreforge:$libreforgeVersion:shadow"))

            val implementation = project.configurations.getByName("implementation")
            implementation.dependencies.add(project.dependencies.create("com.willfp:libreforge-loader:$libreforgeVersion"))

            for (subproject in it.subprojects) {
                val compileOnly = subproject.configurations.getByName("compileOnly")
                compileOnly.dependencies.add(subproject.dependencies.create("com.willfp:libreforge:$libreforgeVersion:shadow"))
                compileOnly.dependencies.add(subproject.dependencies.create("com.willfp:libreforge-loader:$libreforgeVersion"))
            }

            val shadowJarClass = try {
                Class.forName("com.gradleup.shadow.tasks.ShadowJar")
            } catch (_: Exception) {
                try {
                    Class.forName("com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar")
                } catch (_: Exception) {
                    null
                }
            }

            val shadowJarTask = project.tasks.getByName("shadowJar")
            val libreforgeJarFile = project.configurations.getByName("libreforge").singleFile

            if (shadowJarClass != null) {
                // Step 1: Create a task that takes the downloaded libreforge-shadow.jar,
                // unpacks it, relocates Kotlin, and repacks it as a new jar.
                @Suppress("UNCHECKED_CAST")
                val relocatedLibreforgeJar = project.tasks.register(
                    "relocatedLibreforgeJar",
                    shadowJarClass as Class<out Jar>
                ) { task ->
                    task.group = "build"
                    task.description = "Repack libreforge shadow jar with Kotlin relocated"

                    task.destinationDirectory.set(project.layout.buildDirectory.dir("libreforge-relocated"))
                    task.archiveFileName.set("libreforge-$libreforgeVersion-shadow.jar")
                    task.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    // Unpack the downloaded libreforge-shadow.jar so relocate can process its classes
                    task.from(project.zipTree(libreforgeJarFile))

                    val relocate = task.javaClass.getMethod("relocate", String::class.java, String::class.java)
                    relocate.invoke(task, "kotlin", "com.willfp.eco.libs.kotlin")
                    relocate.invoke(task, "org.jetbrains.kotlin", "com.willfp.eco.libs.kotlin.jetbrains")
                }

                // Step 2: Build the final jar — unpack project shadowJar + include relocated libreforge as a file
                @Suppress("UNCHECKED_CAST")
                val libreforgeJar = project.tasks.register(
                    "libreforgeJar",
                    Jar::class.java
                ) { task ->
                    task.group = "build"
                    task.description = "Build the final Libreforge jar"

                    task.destinationDirectory.set(project.file("${project.rootDir}/bin"))
                    task.archiveFileName.set("${project.name} v${project.version}.jar")
                    task.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    task.dependsOn(shadowJarTask)
                    task.dependsOn(relocatedLibreforgeJar)

                    // Unpack the compiled project classes from shadowJar
                    task.from(project.zipTree(shadowJarTask.outputs.files.singleFile))

                    // Include the relocated libreforge jar as a file inside the final jar
                    task.from(relocatedLibreforgeJar.get().outputs.files.singleFile)
                }

                project.tasks.named("build").configure { it.dependsOn(libreforgeJar) }
            } else {
                // Fallback: no relocation possible, include libreforge jar as-is
                val libreforgeJar = project.tasks.register("libreforgeJar", Jar::class.java) { task ->
                    task.group = "build"
                    task.description = "Build the final Libreforge jar"

                    task.destinationDirectory.set(project.file("${project.rootDir}/bin"))
                    task.archiveFileName.set("${project.name} v${project.version}.jar")
                    task.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    task.dependsOn(shadowJarTask)
                    task.from(project.zipTree(shadowJarTask.outputs.files.singleFile))
                    task.from(libreforgeJarFile)
                }

                project.tasks.named("build").configure { it.dependsOn(libreforgeJar) }
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
