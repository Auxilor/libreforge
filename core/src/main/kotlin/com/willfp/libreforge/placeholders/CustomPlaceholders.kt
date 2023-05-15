package com.willfp.libreforge.placeholders

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.placeholders.impl.ConditionalCustomPlaceholder
import com.willfp.libreforge.placeholders.impl.SimpleCustomPlaceholder

object CustomPlaceholders : Registry<CustomPlaceholder>() {
    fun load(config: Config, plugin: EcoPlugin) {
        if (config.has("value")) {
            register(SimpleCustomPlaceholder(config.getString("id"), config.getString("value"), plugin))
        }

        if (config.has("values")) {
            register(ConditionalCustomPlaceholder(config.getString("id"), config, plugin))
        }
    }
}
