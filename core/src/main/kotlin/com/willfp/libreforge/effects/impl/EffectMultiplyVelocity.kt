package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData

object EffectMultiplyVelocity : Effect<NoCompileData>("multiply_velocity") {
    override val arguments = arguments {
        require("multiplier", "You must specify the velocity multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        player.velocity = player.velocity.multiply(config.getDoubleFromExpression("multiplier", data))

        return true
    }
}

