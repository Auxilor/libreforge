package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.LibreforgePluginLike
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
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

        // Legacy chains.yml.
        onReload(LifecyclePosition.START) {
            val chainsYml = this::class.java.classLoader
                .getResourceAsStream("chains.yml")
                .readConfig()

            for (config in chainsYml.getSubsections("chains")) {
                Effects.register(
                    config.getString("id"),
                    Effects.compileChain(
                        config.getSubsections("effects"),
                        NormalExecutorFactory.create(),
                        ViolationContext(this, "chains.yml")
                    ) ?: continue
                )
            }
        }

        onReload(LifecyclePosition.START) {
            for (category in loaderCategories) {
                category.beforeReload()
                category.handle.clear()

                for (config in fetchConfigs(category)) {
                    category.handle.register(config.handle)
                    category.acceptConfig(config.id, config.config)
                }

                val legacy = category.legacyLocation
                if (legacy != null) {
                    val legacyConfig = this::class.java.classLoader
                        .getResourceAsStream("${legacy.filename}.yml")
                        .readConfig()

                    for (config in legacyConfig.getSubsections(legacy.section)) {
                        val id = config.getString("id")

                        val registrable = RegistrableConfig(
                            config,
                            null,
                            id,
                            category
                        )

                        category.handle.register(registrable.handle)
                        category.acceptConfig(id, registrable.config)
                    }
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
        val configs = mutableSetOf<RegistrableConfig>()
        configs += doFetchConfigs(category, category.directory)

        category.legacyLocation?.alternativeDirectories?.forEach { directory ->
            configs += doFetchConfigs(category, directory)
        }

        return configs
    }

    private fun doFetchConfigs(category: ConfigCategory, directory: String): Set<RegistrableConfig> {
        return dataFolder.resolve(directory)
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