package com.willfp.libreforge.levels

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.configs.category.NativeConfigCategory

object LevelTypes : NativeConfigCategory("levels") {
    private val registry = Registry<LevelType>()

    override fun clear(plugin: EcoPlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(LevelType(id, config, plugin))
    }

    operator fun get(id: String): LevelType? {
        return registry[id]
    }
}
