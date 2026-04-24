package com.willfp.libreforge.triggers

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import java.util.*

/*

Prevents multiple identical triggers from being triggered in the same tick.

 */

class DispatchedTriggerFactory(
    private val plugin: EcoPlugin
) {
    private val dispatcherTriggers = listMap<UUID, Int>()


    fun create(dispatcher: Dispatcher<*>, trigger: Trigger, data: TriggerData): DispatchedTrigger? {
        if (!trigger.isEnabled) {
            return null
        }

        val hash = (trigger.hashCode() shl 5) xor data.hashCode()
        if (hash in dispatcherTriggers[dispatcher.uuid]) {
            return null
        }

        dispatcherTriggers[dispatcher.uuid].add(hash)
        val dispatchData = if (data.dispatcher == dispatcher) data else data.copy(dispatcher = dispatcher)
        return DispatchedTrigger(dispatcher, trigger, dispatchData)
    }

    internal fun startTicking() {
        plugin.scheduler.runTaskTimer(1, 1) {
            dispatcherTriggers.clear()
        }
    }
}
