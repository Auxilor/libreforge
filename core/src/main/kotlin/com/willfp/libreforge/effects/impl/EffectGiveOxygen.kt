package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGiveOxygen : Effect(
    "give_oxygen",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of oxygen to give!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.remainingAir = player.remainingAir + config.getIntFromExpression("amount", data)
    }
}
