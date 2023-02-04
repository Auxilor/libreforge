package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent

class EffectCritMultiplier : Effect(
    "crit_multiplier",
    triggers = Triggers.withParameters(
        TriggerParameter.EVENT,
        TriggerParameter.PLAYER
    ),
    noDelay = true
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the crit damage multiplier!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedDamageEvent ?: return
        val player = data.player ?: return

        if (player.velocity.y >= 0) {
            return
        }

        event.damage *= config.getDoubleFromExpression("multiplier", data)
    }
}
