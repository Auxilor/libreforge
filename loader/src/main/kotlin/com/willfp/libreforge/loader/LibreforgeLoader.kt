package com.willfp.libreforge.loader

import com.willfp.eco.core.data.readExternalData
import com.willfp.eco.core.data.writeExternalData
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.bukkit.Bukkit
import java.io.FileOutputStream
import java.util.zip.ZipFile

private const val HIGHEST_LIBREFORGE_VERSION_KEY = "highest-libreforge-version"
private const val HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY = "highest-libreforge-version-plugin"

class LibreforgeNotFoundError(
    override val message: String
) : Error(message)

fun checkHighestVersion(plugin: LibreforgePlugin) {
    val currentHighestVersion = readExternalData<DefaultArtifactVersion>(HIGHEST_LIBREFORGE_VERSION_KEY)
        ?: DefaultArtifactVersion("0.0.0")

    if (plugin.libreforgeVersion > currentHighestVersion) {
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_KEY, plugin.libreforgeVersion)
        writeExternalData(HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY, plugin)
    }
}

fun loadHighestLibreforgeVersion() {
    if (Bukkit.getPluginManager().isPluginEnabled("libreforge")) {
        return
    }

    val plugin = readExternalData<LibreforgePlugin>(HIGHEST_LIBREFORGE_VERSION_PLUGIN_KEY)
        ?: throw IllegalStateException("No libreforge plugins found?")

    val version = readExternalData<DefaultArtifactVersion>(HIGHEST_LIBREFORGE_VERSION_KEY)
        ?: throw IllegalStateException("No libreforge version found?")

    val libreforgeFolder = plugin.dataFolder.parentFile.resolve("libreforge")

    val versionsFolder = libreforgeFolder.resolve("versions")
    versionsFolder.mkdirs()

    libreforgeFolder.mkdirs()
    val libreforgeJar = versionsFolder
        .resolve("libreforge-${version}.jar")

    val zip = ZipFile(plugin.file)

    val libreforgeResourceName = zip.entries().asIterator().asSequence()
        .map { it.name }
        .firstOrNull { it.startsWith("libreforge") && it.endsWith(".jar") }
        ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar!")

    val entry = zip.getEntry(libreforgeResourceName)
    val inputStream = zip.getInputStream(entry)

    FileOutputStream(libreforgeJar).use {
        inputStream.copyTo(it)
        inputStream.close()
    }

    Bukkit.getPluginManager().loadPlugin(libreforgeJar)
}
