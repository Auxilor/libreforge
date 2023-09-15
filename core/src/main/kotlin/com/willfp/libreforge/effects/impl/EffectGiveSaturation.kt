package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveSaturation : Effect<NoCompileData>("give_saturation") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of saturation to give!")

    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (config.getBool("respect-vanilla-limits")) {
            if (player.saturation >= 20.0F) { player.saturation = player.saturation } else
            player.saturation = (player.saturation + config.getIntFromExpression("amount", data)).coerceIn(
                minimumValue = 0.0F,
                maximumValue = (player.foodLevel.toFloat()).coerceAtMost(maximumValue = 20.0F)
            )
        } else {
            player.saturation = player.saturation + config.getIntFromExpression("amount", data)
        }



        return true
    }
}