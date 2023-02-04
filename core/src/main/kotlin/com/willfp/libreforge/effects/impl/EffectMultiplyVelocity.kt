package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectMultiplyVelocity : Effect(
    "multiply_velocity",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the velocity multiplier!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        player.velocity = player.velocity.multiply(config.getDoubleFromExpression("multiplier", data))
    }
}
