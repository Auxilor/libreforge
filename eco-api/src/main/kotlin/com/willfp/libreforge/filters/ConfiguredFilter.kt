package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.Block
import org.bukkit.event.entity.EntityDamageEvent
import java.util.function.Predicate

class ConfiguredFilter(
    private val config: Config
) : Filter {
    override fun matches(data: TriggerData): Boolean {
        val testResults = mutableListOf<Boolean>()

        for (filter in Filters.values()) {
            testResults.add(filter.passes(data, config))
        }

        return testResults.isEmpty() || testResults.stream().allMatch { it }
    }
}
