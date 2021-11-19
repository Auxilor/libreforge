package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

class FilterEmpty : Filter() {
    override fun matches(data: TriggerData): Boolean {
        return true
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        return emptyList()
    }
}
