package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.earning.EarningLimits
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveXp : Effect<NoCompileData>("give_xp") {
    override val description = "Gives the player experience points when triggered, with optional Mending repair support."
    override val categories = setOf("economy")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of xp to give!",
            description = "The amount of experience points to give the player. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
        optional(
            "apply_mending",
            description = "Whether to apply Mending repair before adding XP to the bar (Paper only). Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "earning-limit",
            description = "The ID of the earning-limit group.",
            type = ArgType.STRING
        )
    }

    override fun makeCompileData(config: Config, context: ViolationContext): NoCompileData {
        EarningLimits.validateGroup(config, context)
        return NoCompileData
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        var amount = config.getIntFromExpression("amount", data)
        val groupId = config.getStringOrNull("earning-limit")

        if (groupId != null) {
            amount = minOf(amount.toDouble(), EarningLimits.getRemaining(player, groupId)).toInt()
            EarningLimits.addEarned(player, groupId, amount.toDouble())
        }

        if (Prerequisite.HAS_PAPER.isMet) {
            player.giveExp(amount, config.getBoolOrNull("apply_mending") ?: true)
        } else {
            player.giveExp(amount)
        }

        return true
    }
}
