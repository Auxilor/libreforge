package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.givePoints
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGivePoints : Effect(
    "give_points",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.givePoints(config.getString("type"), config.getDoubleFromExpression("amount", data))
    }
}
