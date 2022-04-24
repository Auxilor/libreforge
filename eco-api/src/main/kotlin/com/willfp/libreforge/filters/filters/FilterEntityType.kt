package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterEntityType : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        return config.withInverse("entities", Config::getStrings) {
            val testables = it.map { lookup -> Entities.lookup(lookup) }

            it.containsIgnoreCase(entity.type.name)
                    || it.containsIgnoreCase(entity.category.name)
                    || testables.any { test -> test.matches(entity) }
        }
    }
}
