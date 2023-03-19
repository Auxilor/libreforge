package com.willfp.libreforge.loader

import com.willfp.eco.core.data.readExternalData
import com.willfp.eco.core.data.writeExternalData
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.bukkit.Bukkit
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

private const val HIGHEST_LIBREFORGE_VERSION_KEY = "highest-libreforge-version"
private const val HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY = "highest-libreforge-version-plugin"

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)

internal fun checkHighestVersion(plugin: LibreforgePlugin) {
    val currentHighestVersion = readExternalData(HIGHEST_LIBREFORGE_VERSION_KEY) {
        DefaultArtifactVersion("0.0.0")
    }

    if (plugin.libreforgeVersion > currentHighestVersion) {
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_KEY, plugin.libreforgeVersion)
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY, plugin)
    }
}

internal fun loadHighestLibreforgeVersion() {
    if (Bukkit.getPluginManager().plugins.any { it.name == "libreforge" }) return

    val plugin = readExternalData<LibreforgePlugin>(HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge plugins found")

    val version = readExternalData<DefaultArtifactVersion>(HIGHEST_LIBREFORGE_VERSION_KEY)
        ?: throw LibreforgeNotFoundError("No libreforge version found")

    val libreforgeFolder = plugin.dataFolder.parentFile.resolve("libreforge")
    val versionsFolder = libreforgeFolder.resolve("versions")

    versionsFolder.mkdirs()

    val libreforgeJar = versionsFolder.resolve("libreforge-$version.jar")
    val libreforgeResourceName = findLibreforgeResourceName(plugin.file)
        ?: throw LibreforgeNotFoundError("Libreforge wasn't found in the plugin jar")

    ZipFile(plugin.file).use { zip ->
        val entry = zip.getEntry(libreforgeResourceName)
        entry ?: throw LibreforgeNotFoundError("Libreforge resource not found in the plugin jar")
        FileOutputStream(libreforgeJar).use { outputStream ->
            zip.getInputStream(entry).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    Bukkit.getPluginManager().loadPlugin(libreforgeJar)
}

private fun findLibreforgeResourceName(pluginFile: File): String? {
    val zip = ZipFile(pluginFile)
    val entries = zip.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement()
        if (entry.name.startsWith("libreforge") && entry.name.endsWith(".jar")) {
            return entry.name
        }
    }
    return null
}
