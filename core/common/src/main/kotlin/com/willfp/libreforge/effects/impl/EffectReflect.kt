package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
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
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return false
        val attacker = event.damager as? LivingEntity ?: return false
        val reflected = event.finalDamage * config.getDoubleFromExpression("multiplier", data)
        attacker.damage(reflected)
        return true
    }
}
