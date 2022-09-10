package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.conditions.isMet
import com.willfp.libreforge.events.TriggerPreProcessEvent
import com.willfp.libreforge.filters.Filter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class Counter internal constructor(
    val trigger: Trigger,
    val multiplier: Double,
    val conditions: Iterable<ConfiguredCondition>,
    val filters: Config
) {
    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun getCount(event: TriggerPreProcessEvent): Double {
        val player = event.player
        val value = event.value
        val data = event.data

        if (!conditions.isMet(player)) {
            return 0.0
        }

        if (!Filter.matches(data, filters)) {
            return 0.0
        }

        if (event.trigger != trigger) {
            return 0.0
        }

        return value * multiplier
    }
}
