package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Tameable

object FilterTamedEntity : Filter<Collection<TestableEntity>, List<String>>("tamed_entity") {
    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: Collection<TestableEntity>): Boolean {
        val victim = data.victim ?: return false

        if (victim !is Tameable || !victim.isTamed) return false

        return value.containsIgnoreCase(victim.type.name)
                || compileData.any { it.matches(victim) }
    }

    override fun makeCompileData(
        config: Config,
        context: ViolationContext,
        values: List<String>
    ): Collection<TestableEntity> {
        return values.map { Entities.lookup(it) }
    }
}