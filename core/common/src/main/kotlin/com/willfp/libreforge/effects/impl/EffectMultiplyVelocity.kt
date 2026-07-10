package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectMultiplyVelocity : Effect<NoCompileData>("multiply_velocity") {
    override val description = "Multiplies the player's current velocity vector by the specified factor."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the velocity multiplier!",
            description = "The factor to multiply all velocity components by (e.g. 2 = double speed). Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        player.velocity = player.velocity.multiply(config.getDoubleFromExpression("multiplier", data))

        return true
    }
}

