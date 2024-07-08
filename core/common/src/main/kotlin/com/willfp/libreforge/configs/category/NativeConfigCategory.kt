package com.willfp.libreforge.configs.category

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import java.util.zip.ZipFile

abstract class NativeConfigCategory(
    val directory: String
) {
    abstract fun clear(plugin: EcoPlugin)

    abstract fun acceptConfig(plugin: EcoPlugin, id: String, config: Config)

    internal fun reload(plugin: EcoPlugin) {
        this.clear(plugin)

        for (config in this.fetchConfigs(plugin)) {
            this.acceptConfig(plugin, config.id, config.config)
        }
    }

    internal fun copyConfigs(plugin: EcoPlugin) {
        val folder = plugin.dataFolder.resolve(this.directory)
        if (!folder.exists()) {
            getDefaultConfigNames(plugin).forEach { configName ->
                FoundConfig(configName, this.directory, plugin).copy()
            }
        }
    }

    private fun getDefaultConfigNames(plugin: EcoPlugin): Collection<String> {
        val files = mutableListOf<String>()

        try {
            ZipFile(plugin.file).use { zipFile ->
                zipFile.entries().asSequence()
                    .filter { it.name.startsWith("${this.directory}/") }
                    .mapTo(files) { it.name.removePrefix("${this.directory}/") }
            }
        } catch (_: Exception) {
            // Sometimes, ZipFile likes to completely fail. No idea why, but here's the 'solution'!
        }

        return files.filter { it.endsWith(".yml") }.map { it.removeSuffix(".yml") }
    }

    private fun fetchConfigs(plugin: EcoPlugin): Set<IdentifiedConfig> {
        return plugin.dataFolder.resolve(directory)
            .walk()
            .filter { it.isFile && it.name.endsWith(".yml") && !it.nameWithoutExtension.startsWith("_") }
            .map { file ->
                val id = file.nameWithoutExtension
                val config = file.readConfig()
                IdentifiedConfig(config, id)
            }.toSet()
    }
}
