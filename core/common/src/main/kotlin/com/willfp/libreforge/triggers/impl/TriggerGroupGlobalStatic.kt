package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.libreforge.GlobalDispatcher
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import java.util.concurrent.ConcurrentHashMap

object TriggerGroupGlobalStatic : TriggerGroup("global_static") {
    private val registry = ConcurrentHashMap<Int, TriggerGlobalStatic>()
    private val dynamicRegistry = ConcurrentHashMap<String, TriggerDynamicGlobalStatic>()
    private var tick = 0

    override fun create(value: String): Trigger? {
        val interval = value.toIntOrNull()

        if (interval == null) {
            return dynamicRegistry.getOrPut(value) { TriggerDynamicGlobalStatic(value) }
        }

        if (interval < 1) {
            return null
        }

        return registry.getOrPut(interval) { TriggerGlobalStatic(interval) }
    }

    override fun postRegister() {
        plugin.scheduler.runTaskTimer(1, 1) {
            tick++

            for ((interval, trigger) in registry) {
                if (tick % interval == 0) {
                    trigger.dispatch(
                        GlobalDispatcher,
                        TriggerData()
                    )
                }
            }

            for ((_, trigger) in dynamicRegistry) {
                trigger.dispatchIfMet(tick)
            }
        }
    }

    private class TriggerGlobalStatic(interval: Int) : Trigger("global_static_$interval") {
        override val parameters = emptySet<TriggerParameter>()
    }

    private class TriggerDynamicGlobalStatic(
        private val expression: String
    ) : Trigger("global_static_expr_${expression.hashCode()}") {
        override val parameters = emptySet<TriggerParameter>()

        fun dispatchIfMet(tick: Int) {
            val interval = evaluateExpressionOrNull(
                expression,
                placeholderContext()
            )?.toInt() ?: return

            if (interval < 1 || tick % interval != 0) {
                return
            }

            this.dispatch(
                GlobalDispatcher,
                TriggerData()
            )
        }
    }
}
