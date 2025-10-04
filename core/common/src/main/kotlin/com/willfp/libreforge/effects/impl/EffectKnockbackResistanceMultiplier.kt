package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectKnockbackResistanceMultiplier : AttributeEffect("knockback_resistance_multiplier",
    Attribute.KNOCKBACK_RESISTANCE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the knockback resistance multiplier!")
    }

    override fun getValue(config: Config, entity: LivingEntity): Double {
        val multiplier = config.getDoubleFromExpression("multiplier", entity as? Player)
        val value = multiplier - 1.0

        return value
    }
}