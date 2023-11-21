package com.willfp.libreforge.triggers

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.EntityDispatcher
import org.bukkit.entity.Player
import java.util.UUID

/*

Prevents multiple identical triggers from being triggered in the same tick.

 */

class DispatchedTriggerFactory(
    private val plugin: EcoPlugin
) {
    private val dispatcherTriggers = listMap<UUID, Int>()

    @Deprecated(
        "Use create(dispatcher, trigger, data) instead",
        ReplaceWith("create(dispatcher, trigger, data)"),
        DeprecationLevel.ERROR
    )
    fun create(player: Player, trigger: Trigger, data: TriggerData): DispatchedTrigger? {
        return create(EntityDispatcher(player), trigger, data)
    }

    fun create(dispatcher: Dispatcher<*>, trigger: Trigger, data: TriggerData): DispatchedTrigger? {
        if (!trigger.isEnabled) {
            return null
        }

        val hash = (trigger.hashCode() shl 5) xor data.hashCode()
        if (hash in dispatcherTriggers[dispatcher.uuid]) {
            return null
        }

        dispatcherTriggers[dispatcher.uuid] += hash
        return DispatchedTrigger(dispatcher, trigger, data)
    }

    internal fun startTicking() {
        plugin.scheduler.runTimer(1, 1) {
            dispatcherTriggers.clear()
        }
    }
}
