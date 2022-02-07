package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectExtinguish: Effect(
    "extinguish",
    Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        player.fireTicks = 0
    }
}
