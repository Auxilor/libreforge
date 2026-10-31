package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerGroupCustom
import java.util.concurrent.ConcurrentHashMap

object EffectTriggerCustom : Effect<NoCompileData>("trigger_custom") {
    override val description = "Dispatches a named custom trigger, which can activate other effect chains listening for that trigger ID."
    override val categories = setOf("meta")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "trigger",
            "You must specify the custom trigger ID!",
            description = "The ID of the custom trigger to dispatch.",
            type = ArgType.STRING
        )
        optional(
            "value",
            description = "An optional value to pass to the dispatched trigger. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "0"
        )
    }

    private val activeDispatches = ThreadLocal.withInitial { ConcurrentHashMap.newKeySet<Int>() }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val trigger = TriggerGroupCustom.create(config.getFormattedString("trigger", data))
        val dispatchData = data.copy(value = config.getDoubleFromExpression("value", data))
        val dispatchKey = trigger.hashCode() shl 5 xor dispatchData.hashCode()
        val active = activeDispatches.get()

        if (!active.add(dispatchKey)) {
            return true
        }

        try {
            trigger.dispatch(data.dispatcher, dispatchData, allowDuplicates = true)
        } finally {
            active.remove(dispatchKey)
        }

        return true
    }
}
