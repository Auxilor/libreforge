package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.readConfig
import com.willfp.libreforge.LibreforgeInitializer
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import java.io.File
import java.io.FileOutputStream
import java.net.URLClassLoader

private const val CLASS_NAME = "com.willfp.libreforge.LibreforgeInitializer"

fun initLibreforge(plugin: EcoPlugin) {
    val libreforgeFolder = File(plugin.dataFolder.parentFile, "libreforge")
    libreforgeFolder.mkdirs()

    val config = File(libreforgeFolder, "version.yml").readConfig()
    val pluginConfig = plugin::class.java.getResourceAsStream("libreforge.yml").readConfig()

    val installedVersion = DefaultArtifactVersion(config.getString("version"))
    val currentVersion = DefaultArtifactVersion(pluginConfig.getString("version"))

    if (installedVersion < currentVersion || !config.has("version")) {
        libreforgeFolder.mkdirs()
        val libreforgeJar = File(libreforgeFolder, "libreforge.jar")

        val inputStream = plugin::class.java.getResourceAsStream("libreforge.jar")
            ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar!")

        FileOutputStream(libreforgeJar).use {
            inputStream.copyTo(it)
            inputStream.close()
        }
    }

    try {
        Class.forName(CLASS_NAME)
    } catch (e: ClassNotFoundException) {
        val libreforgeJar = File(libreforgeFolder, "libreforge.jar")

        Class.forName(
            CLASS_NAME,
            true,
            URLClassLoader(arrayOf(libreforgeJar.toURI().toURL()), plugin::class.java.classLoader)
        )
    }

    LibreforgeInitializer.addPlugin(plugin)
}

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)
