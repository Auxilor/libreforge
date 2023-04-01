package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.internal.LoadedLibreforgePluginImpl
import com.willfp.libreforge.loader.internal.checkHighestVersion
import com.willfp.libreforge.loader.internal.configs.FoundConfig
import com.willfp.libreforge.loader.internal.configs.RegistrableConfig
import com.willfp.libreforge.loader.internal.loadHighestLibreforgeVersion
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
            loadHighestLibreforgeVersion(dataFolder.parentFile)

            // Remove legacy lrcdb.yml.
            this.dataFolder.resolve("lrcdb.yml").delete()
        }

        onEnable(LifecyclePosition.START) {
            Plugins.register(LoadedLibreforgePluginImpl(this))
            loadCategories()
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
                category.beforeReload(this)
                category.clear(this)
                category.handle.clear()

                for (config in fetchConfigs(category)) {
                    category.handle.register(config.handle)
                    category.acceptConfig(this, config.id, config.config)
                }

                val legacy = category.legacyLocation
                if (legacy != null) {
                    val legacyConfig = this::class.java.classLoader
                        .getResourceAsStream(legacy.filename)
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
                        category.acceptConfig(this, id, registrable.config)
                    }
                }

                category.afterReload(this)
            }
        }
    }

    private fun competeForVersion() {
        checkHighestVersion(this)
    }

    private fun loadCategories() {
        for (category in loadConfigCategories()) {
            addCategory(category)
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

    override fun mutateProps(props: PluginProps): PluginProps {
        return props.apply {
            isSupportingExtensions = true
        }
    }

    override fun getMinimumEcoVersion(): String {
        return "6.53.0"
    }

    /**
     * Load default config categories.
     */
    open fun loadConfigCategories(): List<ConfigCategory> {
        return listOf()
    }

    /**
     * Add a new [category].
     */
    fun addCategory(category: ConfigCategory) {
        category.makeHandle(this)
        copyConfigs(category)
        loaderCategories += category
        categories.register(category.handle)
    }
}
