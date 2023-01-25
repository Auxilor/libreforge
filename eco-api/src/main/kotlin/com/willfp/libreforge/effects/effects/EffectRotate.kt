package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectRotate : Effect(
    "rotate",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("angle", "You must specify the angle to rotate by!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val yaw = player.location.yaw + config.getDoubleFromExpression("angle", data).toFloat()


        player.location.yaw = yaw % 360f
    }
}
