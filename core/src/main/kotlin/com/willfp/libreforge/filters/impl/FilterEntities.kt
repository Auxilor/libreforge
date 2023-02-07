package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterEntities : Filter<Collection<TestableEntity>, List<String>>("entities") {
    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun filter(data: TriggerData, value: List<String>, compileData: Collection<TestableEntity>): Boolean {
        val victim = data.victim ?: return true

        return value.containsIgnoreCase(victim.type.name)
                || value.containsIgnoreCase(victim.category.name)
                || compileData.any { it.matches(victim) }
    }
}
