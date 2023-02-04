package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.util.Vector

class EffectSetVelocity : Effect(
    "set_velocity",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("x", "You must specify the velocity x component!")
        require("y", "You must specify the velocity y component!")
        require("z", "You must specify the velocity z component!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.velocity = Vector(
            config.getDoubleFromExpression("x", data),
            config.getDoubleFromExpression("y", data),
            config.getDoubleFromExpression("z", data)
        )
    }
}
