package com.willfp.libreforge.internal.filter

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.filter.Filter

class CompoundFilter(
    vararg filters: Filter
) : Filter() {
    private val filters: Array<out Filter>

    init {
        this.filters = filters
    }

    override fun matches(obj: Any): Boolean {
        for (filter in filters) {
            if (!filter.matches(obj)) {
                return false
            }
        }
        return true
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        return emptyList()
    }
}
