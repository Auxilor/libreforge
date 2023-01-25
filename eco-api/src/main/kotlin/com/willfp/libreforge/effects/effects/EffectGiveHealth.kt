package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.attribute.Attribute

class EffectGiveHealth : Effect(
    "give_health",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of health to give!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.health = (player.health + config.getDoubleFromExpression("amount", data))
            .coerceAtMost(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value)
    }
}
