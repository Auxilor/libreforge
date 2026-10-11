package com.willfp.libreforge.triggers

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.Dispatcher
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/*

Prevents multiple identical triggers from being triggered in the same tick.

 */

class DispatchedTriggerFactory(
    private val plugin: EcoPlugin
) {
    // Written by triggers on any thread.
    private val dispatcherTriggers = ConcurrentHashMap<UUID, MutableSet<Int>>()

    fun create(
        dispatcher: Dispatcher<*>,
        trigger: Trigger,
        data: TriggerData,
        allowDuplicates: Boolean = false
    ): DispatchedTrigger? {
        if (!trigger.isEnabled) {
            return null
        }

        // Some triggers legitimately happen several times in the same tick with identical data,
        // for example killing an entire stack of entities at once.
        if (!allowDuplicates) {
            val hash = (trigger.hashCode() shl 5) xor data.hashCode()
            if (!dispatcherTriggers.computeIfAbsent(dispatcher.uuid) { ConcurrentHashMap.newKeySet() }.add(hash)) {
                return null
            }
        }

        val dispatchData = if (data.dispatcher == dispatcher) data else data.copy(dispatcher = dispatcher)
        return DispatchedTrigger(dispatcher, trigger, dispatchData)
    }

    internal fun startTicking() {
        plugin.scheduler.global().runTimer(1, 1) {
            dispatcherTriggers.clear()
        }
    }
}
