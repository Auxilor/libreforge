package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerGroupGlobalStatic : TriggerGroup("global_static") {
    private val registry = mutableMapOf<Int, TriggerGlobalStatic>()
    private var tick = 0

    override fun create(value: String): Trigger? {
        val interval = value.toIntOrNull() ?: return null
        val trigger = TriggerGlobalStatic(interval)
        registry[interval] = trigger
        return trigger
    }

    override fun postRegister() {
        plugin.scheduler.runTimer(1, 1) {
            tick++

            for ((interval, trigger) in registry) {
                if (tick % interval == 0) {
                    trigger.dispatch(
                        GlobalDispatcher,
                        TriggerData()
                    )
                }
            }
        }
    }

    private class TriggerGlobalStatic(interval: Int) : Trigger("global_static_$interval") {
        override val parameters = emptySet<TriggerParameter>()
    }
}
