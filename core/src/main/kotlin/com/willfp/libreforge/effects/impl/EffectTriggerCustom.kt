package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerCustom

class EffectTriggerCustom : Effect(
    "trigger_custom",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    ),
    noDelay = true
) {
    override val arguments = arguments {
        require("trigger", "You must specify the custom trigger ID!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val trigger = TriggerCustom.getWithID(config.getString("trigger"))
        val value = config.getDoubleFromExpression("value", data)

        trigger.invoke(player, data, value)
    }
}
