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

object EffectGivePoints : Effect<NoCompileData>("give_points") {
    override val description = "Adds points to a specific player point type when triggered."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The player point type to add to.",
            type = ArgType.STRING
        )
        optional(
            "amount",
            description = "The amount of points to add to the player. Supports expressions. Defaults to 0.",
            type = ArgType.EXPRESSION,
            default = "0"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        player.points[config.getString("type")] += config.getDoubleFromExpression("amount", data)

        return true
    }
}
