package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.filters.containsIgnoreCase
import com.willfp.libreforge.triggers.TriggerData

class FilterEntityType : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true


        val entityNames = config.getStrings("entities", false)

        return entityNames.containsIgnoreCase(entity.type.name)
                || entityNames.containsIgnoreCase(entity.category.name)
    }
}