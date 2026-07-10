package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectMultiplyPoints : Effect<NoCompileData>("multiply_points") {
    override val description = "Multiplies a player's point value for the specified point type by the given amount."
    override val categories = setOf("economy", "points")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The player point type to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the player's point value. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val type = config.getString("type")

        player.points[type] *= config.getDoubleFromExpression("multiplier", data)

        return true
    }
}
