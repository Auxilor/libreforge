package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.readConfig
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import java.io.FileOutputStream

fun initLibreforge(plugin: EcoPlugin) {
    val libreforgeFolder = plugin.dataFolder.parentFile.resolve("libreforge")
    libreforgeFolder.mkdirs()

    val config = libreforgeFolder.resolve("version.yml").readConfig()
    val pluginConfig = plugin::class.java.getResourceAsStream("libreforge.yml").readConfig()

    val installedVersion = DefaultArtifactVersion(config.getString("version"))
    val currentVersion = DefaultArtifactVersion(pluginConfig.getString("version"))

    if (installedVersion < currentVersion || !config.has("version")) {
        libreforgeFolder.mkdirs()
        val libreforgeJar = libreforgeFolder.resolve("libreforge.jar")

        val inputStream = plugin::class.java.getResourceAsStream("libreforge.jar")
            ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar!")

        FileOutputStream(libreforgeJar).use {
            inputStream.copyTo(it)
            inputStream.close()
        }
    }

    LibreforgeInitializer.addPlugin(plugin)
}

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)
