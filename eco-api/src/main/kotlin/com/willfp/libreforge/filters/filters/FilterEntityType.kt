package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterEntityType: FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        val entityNames = config.getStringsOrNull("entities", false)
            ?.map { it.lowercase() } ?: emptyList()

        return entityNames.contains(entity.type.name.lowercase()) || entityNames.contains(entity.category.name.lowercase())
    }
}