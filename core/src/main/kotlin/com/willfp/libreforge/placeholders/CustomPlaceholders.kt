package com.willfp.libreforge.placeholders

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry

object CustomPlaceholders : Registry<CustomPlaceholder>() {
    fun load(config: Config) {
        register(CustomPlaceholder(config.getString("id"), config.getString("value")))
    }
}
