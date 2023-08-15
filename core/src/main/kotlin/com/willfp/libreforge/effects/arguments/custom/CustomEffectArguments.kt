package com.willfp.libreforge.effects.arguments.custom

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.configs.category.NativeConfigCategory
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.levels.LevelType

object CustomEffectArguments : NativeConfigCategory("arguments") {
    private val registry = Registry<CustomEffectArgument>()

    override fun clear(plugin: EcoPlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(CustomEffectArgument(id, config, plugin))
    }

    operator fun get(id: String): CustomEffectArgument? {
        return registry[id]
    }
}
