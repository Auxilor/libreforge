package com.willfp.libreforge.loader

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.LifecyclePosition
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.config.emptyConfig
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.registry.Registry
import com.willfp.eco.core.version.Version
import com.willfp.libreforge.Plugins
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.configs.LibreforgeConfigCategory
import com.willfp.libreforge.configs.category.FoundConfig
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.internal.InvalidLibreforgePluginError
import com.willfp.libreforge.loader.internal.LoadedLibreforgePluginImpl
import com.willfp.libreforge.loader.internal.checkHighestVersion
import com.willfp.libreforge.loader.internal.configs.RegistrableConfig
import com.willfp.libreforge.loader.internal.loadHighestLibreforgeVersion
import com.willfp.libreforge.loader.internal.tryLoadForceVersion
import java.io.File
import java.util.zip.ZipFile

abstract class LibreforgePlugin : EcoPlugin() {
    private val loaderCategories = mutableListOf<ConfigCategory>()

    val categories = Registry<LibreforgeConfigCategory>()

    val libreforgeVersion = Version(
        this.props.getEnvironmentVariable("libreforge version")
            ?: throw InvalidLibreforgePluginError("libreforge version environment variable not set!")
    )

    init {
        categories.lock(RegistryLock) // Only allow registration through addCategory.

        tryLoadForceVersion(dataFolder.parentFile)

        competeForVersion()

        onLoad(LifecyclePosition.START) {
            loadHighestLibreforgeVersion(dataFolder.parentFile)

            // Remove legacy lrcdb.yml.
            this.dataFolder.resolve("lrcdb.yml").delete()
        }

        onLoad(LifecyclePosition.END) {
            loadCategories()

            for (category in loaderCategories.filter { it.shouldPreload }) {
                loadCategory(category, preload = true)
            }
        }

        onEnable(LifecyclePosition.START) {
            Plugins.register(LoadedLibreforgePluginImpl(this))
        }

        // Legacy chains.yml.
        onReload(LifecyclePosition.START) {
            val chainsYml = this.dataFolder.resolve("chains.yml")
                .let { if (it.exists()) it.readConfig() else emptyConfig() }

            for (config in chainsYml.getSubsections("chains")) {
                Effects.register(
                    config.getString("id"),
                    Effects.compileChain(
                        config.getSubsections("effects"),
                        ViolationContext(this, "chains.yml")
                    ) ?: continue
                )
            }
        }

        onReload(LifecyclePosition.START) {
            for (category in loaderCategories) {
                loadCategory(category)
            }
        }
    }

    private fun loadCategory(category: ConfigCategory, preload: Boolean = false) {
        withLogs(category, "before reload") {
            category.beforeReload(this)
        }

        withLogs(category, "clear") {
            category.clear(this)
            category.handle.clear()
        }

        for (config in fetchConfigs(category)) {
            withLogs(category, "loading config ${config.id}") {
                category.handle.register(config.handle)
                if (preload) {
                    category.acceptPreloadConfig(this, config.id, config.config)
                } else {
                    category.acceptConfig(this, config.id, config.config)
                }
            }
        }

        val legacy = category.legacyLocation
        if (legacy != null) {
            for (config in legacy.getConfig(this).getSubsections(legacy.section)) {
                val id = config.getString("id")

                val registrable = RegistrableConfig(
                    config,
                    null,
                    id,
                    category
                )

                withLogs(category, "loading legacy config $id") {
                    category.handle.register(registrable.handle)
                    category.acceptConfig(this, id, registrable.config)
                }
            }
        }

        withLogs(category, "after reload") {
            category.afterReload(this)
        }
    }

    private fun withLogs(category: ConfigCategory, position: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            this.logger.warning("Exception loading ${category.id} at $position!")
            e.printStackTrace()
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
        val configNames = getDefaultConfigNames(category)

        if (!folder.exists()) {
            for (name in configNames) {
                FoundConfig(name, category.directory, this).copy()
            }
        }

        for (exampleConfigs in configNames.filter { it.startsWith("_") }) {
            FoundConfig(exampleConfigs, category.directory, this).copy()
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

    private fun fetchConfigs(category: ConfigCategory): List<RegistrableConfig> {
        val configs = mutableSetOf<RegistrableConfig>()
        configs += doFetchConfigs(category, category.directory)

        category.legacyLocation?.alternativeDirectories?.forEach { directory ->
            configs += doFetchConfigs(category, directory)
        }

        return configs
            .sortedBy { it.id }
            .sortedBy { it.config.getInt("load-weight", 100) }
    }

    private fun doFetchConfigs(category: ConfigCategory, directory: String): Set<RegistrableConfig> {
        return dataFolder.resolve(directory)
            .walk()
            .filter { it.isFile && it.name.endsWith(".yml") && !it.nameWithoutExtension.startsWith("_") }
            .map { file ->
                val id = file.nameWithoutExtension
                val config = file.readConfig()
                RegistrableConfig(config, file, id, category)
            }.toSet()
    }

    override fun getFile(): File {
        return super.getFile()
    }

    override fun mutateProps(props: PluginProps): PluginProps {
        return props.apply {
            isSupportingExtensions = true
            try {
                ecoApiVersion = props.ecoApiVersion.coerceAtLeast(Version("6.77.0"))
            } catch (_: NoSuchMethodError) {
                // Ignore, this will happen when running pre-6.77.0.
            }
        }
    }

    @Deprecated(message = "Use eco-api-version in eco.yml instead")
    @Suppress("REMOVAL")
    override fun getMinimumEcoVersion(): String {
        return "6.77.0"
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
        categories.unlock(RegistryLock)
        category.makeHandle(this)
        copyConfigs(category)
        loaderCategories += category
        categories.register(category.handle)
        categories.lock(RegistryLock)
    }

    private object RegistryLock
}
