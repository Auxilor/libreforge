package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.entity.EntityDamageEvent

object EffectCritMultiplier : Effect<NoCompileData>("crit_multiplier") {
    override val description = "Multiplies damage when the player lands a critical hit (falling attack)."
    override val categories = setOf("combat")
    override val additionalInfo = listOf("Requires a trigger that provides both PLAYER and EVENT.")

    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the crit damage multiplier!",
            description = "The damage multiplier applied on a critical hit. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5 + %level% * 0.05"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageEvent ?: return false
        val player = data.player ?: return false

        if (player.velocity.y >= -0.1) {
            return false
        }

        event.damage *= config.getDoubleFromExpression("multiplier", data)
        return true
    }
}
