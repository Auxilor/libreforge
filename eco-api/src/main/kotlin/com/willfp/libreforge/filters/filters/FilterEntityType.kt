package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.filters.containsIgnoreCase
import com.willfp.libreforge.triggers.TriggerData

class FilterEntityType: FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        return config.getStringsOrNull("entities", false)
            ?.containsIgnoreCase(entity.type.name) ?: true
    }
}