package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.earning.EarningLimitGroup
import com.willfp.libreforge.earning.EarningLimits
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGivePoints : Effect<EarningLimitGroup?>("give_points") {
    override val description = "Adds points to a specific player point type when triggered."
    override val categories = setOf("economy", "points")

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
            default = "0",
            example = "%level% * 10"
        )
        optional(
            "earning-limit",
            description = "The ID of the earning-limit group.",
            type = ArgType.STRING
        )
    }

    override fun makeCompileData(config: Config, context: ViolationContext): EarningLimitGroup? {
        return EarningLimits.validateGroup(config, context)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: EarningLimitGroup?): Boolean {
        val player = data.player ?: return false

        val type = config.getFormattedString("type", data)
        var amount = config.getDoubleFromExpression("amount", data)

        if (compileData != null) {
            amount = minOf(amount, EarningLimits.getRemaining(player, compileData))
            if (amount == 0.0) return true
            EarningLimits.addEarned(player, compileData, amount)
        }

        player.points.add(type, amount)

        return true
    }
}
