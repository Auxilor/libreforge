package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.LibreforgePluginLike
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.configs.FoundConfig
import com.willfp.libreforge.loader.configs.RegistrableConfig
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import java.io.FileOutputStream
import java.util.zip.ZipFile

abstract class LibreforgePlugin : EcoPlugin(), LibreforgePluginLike {
    private val loaderCategories = mutableListOf<ConfigCategory>()
    final override val categories = object : Registry<LibreforgeConfigCategory>() {}

    init {
        loadLibreforge()

        loadCategories()

        onReload(LifecyclePosition.START) {
            for (category in loaderCategories) {
                category.handle.clear()

                val configs = doFetchConfigs(category)

                for (config in configs) {
                    category.handle.register(config.handle)

                    category.acceptConfig(config.config)
                }
            }
        }
    }

    private fun loadCategories() {
        for (category in loadConfigCategories()) {
            category.makeHandle(this)

            copyConfigs(category)

            loaderCategories += category
            categories.register(category.handle)
        }
    }

    private fun loadLibreforge() {
        val libreforgeFolder = this.dataFolder.parentFile.resolve("libreforge")
        libreforgeFolder.mkdirs()

        val config = libreforgeFolder.resolve("version.yml").readConfig()
        val pluginConfig = this::class.java.getResourceAsStream("libreforge.yml").readConfig()

        val installedVersion = DefaultArtifactVersion(config.getString("version"))
        val currentVersion = DefaultArtifactVersion(pluginConfig.getString("version"))

        if (installedVersion < currentVersion || !config.has("version")) {
            libreforgeFolder.mkdirs()
            val libreforgeJar = libreforgeFolder.resolve("libreforge.jar")

            val inputStream = this::class.java.getResourceAsStream("libreforge.jar")
                ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar!")

            FileOutputStream(libreforgeJar).use {
                inputStream.copyTo(it)
                inputStream.close()
            }
        }
    }

    private fun copyConfigs(category: ConfigCategory) {
        val folder = this.dataFolder.resolve(category.directory)
        if (!folder.exists()) {
            val files = getDefaultConfigNames(category)

            for (configName in files) {
                FoundConfig(configName, category, this)
            }
        }
    }

    private fun getDefaultConfigNames(category: ConfigCategory): Collection<String> {
        val files = mutableListOf<String>()

        try {
            for (entry in ZipFile(this.file).entries().asIterator()) {
                if (entry.name.startsWith("${category.directory}/")) {
                    files.add(entry.name.removePrefix("${category.directory}/"))
                }
            }
        } catch (_: Exception) {
            // Sometimes, ZipFile likes to completely fail. No idea why, but here's the 'solution'!
        }

        files.removeIf { !it.endsWith(".yml") }
        files.replaceAll { it.replace(".yml", "") }

        return files
    }

    private fun doFetchConfigs(category: ConfigCategory): Set<RegistrableConfig> {
        val configs = mutableSetOf<RegistrableConfig>()

        for (file in this.dataFolder.resolve(category.directory).walk()) {
            if (file.nameWithoutExtension == "_example") {
                continue
            }

            if (!file.name.endsWith(".yml")) {
                continue
            }

            val id = file.nameWithoutExtension
            val config = file.readConfig()
            configs += RegistrableConfig(config, file, id, category)
        }

        return configs
    }

    abstract fun loadConfigCategories(): List<ConfigCategory>
}
