package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.LibreforgePluginLike
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.configs.FoundConfig
import com.willfp.libreforge.loader.configs.RegistrableConfig
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import java.io.File
import java.util.zip.ZipFile

abstract class LibreforgePlugin : EcoPlugin() {
    private val loaderCategories = mutableListOf<ConfigCategory>()

    val categories = Registry<LibreforgeConfigCategory>()

    val libreforgeVersion = DefaultArtifactVersion(this.props.getEnvironmentVariable("libreforge version"))

    init {
        competeForVersion()

        onLoad(LifecyclePosition.START) {
            loadHighestLibreforgeVersion()
        }

        onReload(LifecyclePosition.START) {
            for (category in loaderCategories) {
                category.beforeReload()
                category.handle.clear()

                for (config in fetchConfigs(category)) {
                    category.handle.register(config.handle)
                    category.acceptConfig(config.config)
                }

                category.afterReload()
            }
        }
    }

    private fun competeForVersion() {
        checkHighestVersion(this)
    }

    override fun handleEnable() {
        Plugins.register(
            object : LibreforgePluginLike {
                override val plugin: LibreforgePlugin = this@LibreforgePlugin
                override val categories = plugin.categories
                override fun getDataFolder() = plugin.dataFolder
                override fun getConfigHandler() = plugin.configHandler
                override fun getLogger() = plugin.logger
            }
        )

        loadCategories()
    }

    private fun loadCategories() {
        for (category in loadConfigCategories()) {
            category.makeHandle(this)
            copyConfigs(category)
            loaderCategories += category
            categories.register(category.handle)
        }
    }

    private fun copyConfigs(category: ConfigCategory) {
        val folder = dataFolder.resolve(category.directory)
        if (!folder.exists()) {
            getDefaultConfigNames(category).forEach { configName ->
                FoundConfig(configName, category, this)
            }
        }
    }

    private fun getDefaultConfigNames(category: ConfigCategory): Collection<String> {
        val files = mutableListOf<String>()

        try {
            ZipFile(file).use { zipFile ->
                zipFile.entries().asSequence()
                    .filter { it.name.startsWith("${category.directory}/") }
                    .mapTo(files) { it.name.removePrefix("${category.directory}/") }
            }
        } catch (_: Exception) {
            // Sometimes, ZipFile likes to completely fail. No idea why, but here's the 'solution'!
        }

        return files.filter { it.endsWith(".yml") }.map { it.removeSuffix(".yml") }
    }

    private fun fetchConfigs(category: ConfigCategory): Set<RegistrableConfig> {
        return dataFolder.resolve(category.directory)
            .walk()
            .filter { it.isFile && it.name.endsWith(".yml") && it.nameWithoutExtension != "_example" }
            .map { file ->
                val id = file.nameWithoutExtension
                val config = file.readConfig()
                RegistrableConfig(config, file, id, category)
            }.toSet()
    }


    public override fun getFile(): File {
        return super.getFile()
    }

    abstract fun loadConfigCategories(): List<ConfigCategory>
}
