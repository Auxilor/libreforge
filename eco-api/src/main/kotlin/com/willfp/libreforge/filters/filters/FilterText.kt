package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterText : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val text = data.text ?: return true

        return config.withInverse("text", Config::getStrings) {
            it.containsIgnoreCase(text)
        }
    }
}
