package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerGroupCustom

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

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val dispatcher = data.dispatcher
        val trigger = TriggerGroupCustom.create(config.getString("trigger"))
        val value = config.getDoubleFromExpression("value", data)

        trigger.dispatch(dispatcher, data.copy(value = value))

        return true
    }
}
