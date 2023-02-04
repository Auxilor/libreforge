package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectSetFreezeTicks : Effect(
    "set_freeze_ticks",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("ticks", "You must specify the freeze ticks!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return

        victim.freezeTicks = config.getIntFromExpression("ticks", data)
    }
}
