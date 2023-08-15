package com.willfp.libreforge.loader.internal

import com.willfp.eco.core.data.readExternalData
import com.willfp.eco.core.data.writeExternalData
import com.willfp.eco.core.version.Version
import com.willfp.libreforge.loader.LibreforgePlugin
import org.bukkit.Bukkit
import java.io.File
import java.io.FileOutputStream

private const val HIGHEST_LIBREFORGE_VERSION_KEY = "highest-libreforge-version"
private const val HIGHEST_LIBREFORGE_VERSION_CLASSLOADER_KEY = "highest-libreforge-version-classloader"

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)

internal class InvalidLibreforgePluginError(
    override val message: String
) : Error(message)

internal fun checkHighestVersion(plugin: LibreforgePlugin) {
    val currentHighestVersion = readExternalData(HIGHEST_LIBREFORGE_VERSION_KEY) {
        Version("0.0.0")
    }

    if (plugin.libreforgeVersion > currentHighestVersion) {
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_KEY, plugin.libreforgeVersion)
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_CLASSLOADER_KEY, plugin::class.java.classLoader)
    }
}

internal fun loadHighestLibreforgeVersion(pluginFolder: File) {
    if (Bukkit.getPluginManager().plugins.any { it.name == "libreforge" }) return

    val libreforgeFolder = pluginFolder.resolve("libreforge")
    val versionsFolder = libreforgeFolder.resolve("versions")

    versionsFolder.mkdirs()

    for (file in versionsFolder.walk()) {
        if (file.name == "libreforge-force.jar") {
            Bukkit.getPluginManager().loadPlugin(file)
            return
        }
    }

    val classLoader = readExternalData<ClassLoader>(HIGHEST_LIBREFORGE_VERSION_CLASSLOADER_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge plugin classloader found")

    val version = readExternalData<Version>(HIGHEST_LIBREFORGE_VERSION_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge version found")

    val libreforgeJar = versionsFolder.resolve("libreforge-$version.jar")
    val libreforgeResourceName = "libreforge-$version-shadow.jar"

    FileOutputStream(libreforgeJar).use { outputStream ->
        classLoader.getResourceAsStream(libreforgeResourceName).use { inputStream ->
            inputStream?.copyTo(outputStream)
                ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar")
        }
    }

    Bukkit.getPluginManager().loadPlugin(libreforgeJar)
}
