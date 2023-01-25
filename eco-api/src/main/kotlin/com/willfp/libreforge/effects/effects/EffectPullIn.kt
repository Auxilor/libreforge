package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectPullIn : Effect(
    "pull_in",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("velocity", "You must specify the movement velocity!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val victim = data.victim ?: return

        val vector = player.location.toVector().clone()
            .subtract(victim.location.toVector())
            .normalize()
            .multiply(config.getDoubleFromExpression("velocity", data))

        victim.velocity = vector
    }
}
