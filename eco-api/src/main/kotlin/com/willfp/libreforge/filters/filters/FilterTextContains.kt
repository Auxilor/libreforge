package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterTextContains : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val text = data.text ?: return true

        return config.withInverse("text_contains", Config::getStrings) {
            it.any { test -> text.lowercase().contains(test.lowercase()) }
        }
    }
}
