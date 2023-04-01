package com.willfp.libreforge.loader.internal

import com.willfp.eco.core.data.readExternalData
import com.willfp.eco.core.data.writeExternalData
import com.willfp.libreforge.loader.LibreforgePlugin
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.bukkit.Bukkit
import java.io.File
import java.io.FileOutputStream

private const val HIGHEST_LIBREFORGE_VERSION_KEY = "highest-libreforge-version"
private const val HIGHEST_LIBREFORGE_VERSION_FOLDER_KEY = "highest-libreforge-version-folder"

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)

internal fun checkHighestVersion(plugin: LibreforgePlugin) {
    val currentHighestVersion = readExternalData(HIGHEST_LIBREFORGE_VERSION_KEY) {
        DefaultArtifactVersion("0.0.0")
    }

    if (plugin.libreforgeVersion > currentHighestVersion) {
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_KEY, plugin.libreforgeVersion)
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_FOLDER_KEY, plugin.dataFolder)
    }
}

internal fun loadHighestLibreforgeVersion() {
    if (Bukkit.getPluginManager().plugins.any { it.name == "libreforge" }) return

    val folder = readExternalData<File>(HIGHEST_LIBREFORGE_VERSION_FOLDER_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge plugins found")

    val version = readExternalData<DefaultArtifactVersion>(HIGHEST_LIBREFORGE_VERSION_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge version found")

    val libreforgeFolder = folder.parentFile.resolve("libreforge")
    val versionsFolder = libreforgeFolder.resolve("versions")

    versionsFolder.mkdirs()

    val libreforgeJar = versionsFolder.resolve("libreforge-$version.jar")
    val libreforgeResourceName = "libreforge-$version-shadow.jar"

    FileOutputStream(libreforgeJar).use { outputStream ->
        LibreforgePlugin::class.java.classLoader.getResourceAsStream(libreforgeResourceName).use { inputStream ->
            inputStream?.copyTo(outputStream)
                ?: throw LibreforgeNotFoundError("Libreforge wasn't found in the plugin jar")
        }
    }

    Bukkit.getPluginManager().loadPlugin(libreforgeJar)
}
