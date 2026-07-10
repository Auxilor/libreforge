package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRotate : Effect<NoCompileData>("rotate") {
    override val description = "Rotates the player's yaw by a specified angle."
    override val categories = setOf("movement", "player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "angle",
            "You must specify the angle to rotate by!",
            description = "The number of degrees to add to the player's current yaw. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "90"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val yaw = player.location.yaw + config.getDoubleFromExpression("angle", data).toFloat()

        player.setRotation(
            yaw % 360f,
            player.location.pitch
        )

        return true
    }
}
