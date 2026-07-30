package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.dealDamage
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EffectReflect : Effect<NoCompileData>("reflect") {
    override val description = "Reflects a portion of incoming damage back at the attacker."
    override val categories = setOf("combat")

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the damage multiplier!",
            description = "The fraction of incoming damage to reflect back at the attacker. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "0.2 + %level% * 0.02"
        )
        optional(
            "true_damage",
            description = "If true, damage bypasses armor and resistance effects.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "use_source",
            description = "If true, the player is attributed as the damage source.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return false
        val attacker = event.damager as? LivingEntity ?: return false
        val reflected = event.finalDamage * config.getDoubleFromExpression("multiplier", data)
        val trueDamage = config.getBool("true_damage")
        val source = if (config.getBool("use_source")) data.player else null
        attacker.dealDamage(reflected, source, trueDamage)
        return true
    }
}
