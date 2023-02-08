package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterText : Filter<NoCompileData, Collection<String>>("text") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun filter(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val text = data.text ?: return true

        return value.containsIgnoreCase(text)
    }
}
