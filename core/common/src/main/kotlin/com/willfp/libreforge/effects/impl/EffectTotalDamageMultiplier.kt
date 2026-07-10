package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.entity.EntityDamageEvent

object EffectTotalDamageMultiplier : Effect<NoCompileData>("total_damage_multiplier") {
    override val description = "Multiplies the total damage of the triggering damage event by a given amount."
    override val categories = setOf("combat")

    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the damage multiplier!",
            description = "The value to multiply the event's damage by. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override val runOrder = RunOrder.END

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageEvent ?: return false
        event.damage *= config.getDoubleFromExpression("multiplier", data)

        return true
    }
}
