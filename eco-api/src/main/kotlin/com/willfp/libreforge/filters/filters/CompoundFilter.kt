package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

class CompoundFilter(
    vararg filters: Filter
) : Filter() {
    private val filters: Array<out Filter>

    init {
        this.filters = filters
    }

    override fun matches(data: TriggerData): Boolean {
        for (filter in filters) {
            if (!filter.matches(data)) {
                return false
            }
        }
        return true
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        return emptyList()
    }
}
