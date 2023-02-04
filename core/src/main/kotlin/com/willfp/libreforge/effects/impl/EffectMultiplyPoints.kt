package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getPoints
import com.willfp.libreforge.setPoints
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectMultiplyPoints : Effect(
    "multiply_points",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("multiplier", "You must specify the multiplier!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val type = config.getString("type")

        player.setPoints(type, player.getPoints(type) * config.getDoubleFromExpression("multiplier", data))
    }
}
