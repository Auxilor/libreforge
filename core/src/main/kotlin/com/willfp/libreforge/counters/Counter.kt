package com.willfp.libreforge.counters

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Counter internal constructor(
    trigger: Trigger,
    multiplier: Double,
    conditions: ConditionList,
    filters: FilterList,
    private val count: (Double) -> Unit
) : UndefinedCounter(trigger, multiplier, conditions, filters) {
    /**
     * Bind a counter to a plugin.
     */
    fun bind(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(object : Listener {
            @EventHandler
            fun handle(event: TriggerDispatchEvent) {
                val dispatch = event.trigger
                val data = dispatch.data

                val player = dispatch.player
                val value = data.value

                if (!conditions.areMet(player)) {
                    return
                }

                if (!filters.isMet(data)) {
                    return
                }

                if (dispatch.trigger != trigger) {
                    return
                }

                count(value * multiplier)
            }
        })
    }
}
