package com.willfp.libreforge.internal.filter

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.filter.Filter

class FilterEmpty : Filter() {
    override fun matches(obj: Any): Boolean {
        return true
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        return emptyList()
    }
}
